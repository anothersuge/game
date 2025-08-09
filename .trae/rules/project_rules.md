🧾 项目名称：绿萝狼锐评（暂定）
🎯 项目目标
构建一个供朋友间使用的游戏评价网站，用户可以：

浏览游戏信息
发表评价和评分
查看朋友的评价
进行简单的互动（点赞、评论）
📌 核心功能模块
1. 用户系统
用户注册 / 登录 / 注销
用户资料查看与编辑（昵称、头像、简介）
好友系统（添加好友、查看好友列表）
2. 游戏信息管理
游戏列表展示（分页、搜索、筛选）
游戏详情页（基本信息、评分、评价列表）
游戏添加 / 编辑 / 删除（管理员权限）
3. 评价系统
用户对游戏进行评分（1-5星，每半星一个间隔）
撰写评价（支持 Markdown）
点赞 / 评论其他用户的评价
查看游戏的平均评分和评价统计
4. 社交互动
好友动态流（朋友最近评价了哪些游戏）
点赞 / 评论通知
简单的排行榜（评分最高的游戏、活跃用户）
5. 管理后台（可选）
用户管理
游戏数据管理
举报处理（如果需要）
🏗️ 技术架构描述
后端架构（Spring Boot 3）+java21
框架：Spring Boot 3 + Spring Security + Spring Data JPA
数据库：sqlite
模板引擎：Thymeleaf（前后端不分离）
缓存：Redis（用于排行榜、热门游戏等）
日志：Logback + Spring Actuator
部署：Docker + Nginx（可选）

Nginx 配置：
为了在生产环境中使用 80 端口访问应用，同时避免以 root 权限运行应用，可以使用 Nginx 作为反向代理。
1. 将应用运行在 8080 端口（非特权端口）
2. 配置 Nginx 监听 80 端口并将请求转发到 8080 端口
3. Nginx 配置示例可在项目根目录的 nginx.conf 文件中找到

敏感词过滤：
系统实现了敏感词过滤功能，可以对用户名、游戏名和评论内容进行过滤。
- 敏感词库文件位于 src/main/resources/sensitive-words.txt
- 过滤器实现在 com.lvluolang.game.util.SensitiveWordFilter 类中
- 当检测到敏感词时，系统会静默处理（不保存到数据库但返回成功响应）

数据模型简要设计

✅ 非功能性需求
响应时间 < 1s
支持移动端访问（响应式设计）
数据备份机制
简单权限控制（普通用户 / 管理员）
🚀 开发建议
初期可以只做游戏评价和好友系统，后续再加互动功能。
使用 Bootstrap 或 Tailwind CSS 快速构建前端页面。

记录问题
## 架构问题分析
### 1. 性能优化方面
- N+1 查询问题 ：在 HomeController.gameDetail 方法中，获取游戏详情时先查询游戏信息，然后在模板中可能遍历游戏的评论列表，这可能导致额外的数据库查询。建议使用JOIN FETCH优化查询。
- 排行榜查询效率 ： getTopRatedGames 和 getRecentReviews 等方法直接返回大量数据，对于未来可能的增长没有分页处理。
### 2. 数据一致性问题
- 游戏评分更新 ：在 ReviewService.saveReview 方法中，保存评论后更新游戏的平均评分，但这个操作不是原子性的。如果在计算平均分时有并发操作，可能导致数据不一致。建议使用数据库事务或在数据库层面实现。
- 点赞计数更新 ： ReviewService.likeReview 方法中更新点赞数也不是原子性的，存在并发问题。
### 3. 安全性方面
- IP地址限制 ：当前使用IP地址限制点赞功能，但没有考虑IP地址可能变化的情况，以及IPv6支持问题。对于朋友间使用的场景，可以考虑使用简单的会话标识。
- 敏感词过滤 ：虽然已实现敏感词过滤，但过滤逻辑是在控制器层实现的，建议将其移到服务层或使用AOP实现。
### 4. 代码结构问题
- 业务逻辑重叠 ：在 HomeController.submitReview 方法中同时处理了游戏创建和评论保存的逻辑，建议将游戏创建逻辑移到 GameService 中。
- 重复代码 ：在多个服务类中都有类似的获取客户端IP地址的代码，可以提取到工具类中。
### 5. 数据库设计问题
- 缺少索引 ：在 games 表的 name 、 genre 、 developer 等常用查询字段上缺少索引，可能影响查询性能。
- ReviewLike表设计 ：使用IP地址作为唯一约束可能不够稳定，建议考虑其他标识符。

优化项目
1. 使用构造函数注入替代@Autowired字段注入
当前代码使用了字段注入：

Java



@Autowiredprivate GameService gameService;
建议改为构造函数注入，这是Spring团队推荐的方式：

Java



private final GameService gameService;public HomeController(GameService gameService) {    this.gameService = gameService;}
2. 使用records简化数据载体类
对于像ReviewLike这样的简单实体，可以考虑使用Java 14引入的records（Java 21中更加成熟）：

Java



// 当前代码@Data@NoArgsConstructor@AllArgsConstructorpublic class ReviewLike {    private Long id;    private Long reviewId;    private String ipAddress;    private LocalDateTime createdAt;}// 可以改为public record ReviewLike(Long id, Long reviewId, String ipAddress, LocalDateTime createdAt) {}
3. 使用Stream API和Optional链式操作简化代码
在GameService.getOrCreateGame方法中，可以使用更现代的Optional链式操作：

Java



// 当前代码Optional<Game> existingGame = getAllGames().stream()        .filter(game -> game.getName        ().equals(gameName))        .findFirst();if (existingGame.isPresent()) {    return existingGame.get();} else {    // 创建新游戏}// 可以改为return getAllGames().stream()        .filter(game -> game.getName        ().equals(gameName))        .findFirst()        .orElseGet(() -> {            // 创建新游戏的逻辑            Game game = new Game();            game.setName(gameName);            // ... 其他设置            return saveGame(game);        });
4. 使用var关键字简化局部变量声明
在支持的地方使用Java 10引入的var关键字：

Java



// 当前代码List<Game> topRatedGames = gameService.getTopRatedGames();// 可以改为var topRatedGames = gameService.getTopRatedGames();
5. 使用Pattern Matching for instanceof (Java 16)
在SensitiveWordFilterAspect.filterSensitiveWords方法中：

Java



// 当前代码if (arg instanceof String && sensitiveWordFilter.containsSensitiveWord((String) arg)) {// 可以改为if (arg instanceof String str && sensitiveWordFilter.containsSensitiveWord(str)) {
6. 使用switch表达式简化switch语句
虽然当前代码中没有复杂的switch语句，但在需要时可以使用Java 14的switch表达式：

Java



// 传统switchString result;switch (value) {    case 1: result = "one"; break;    case 2: result = "two"; break;    default: result = "other";}// switch表达式String result = switch (value) {    case 1 -> "one";    case 2 -> "two";    default -> "other";};
7. 使用Text Blocks (Java 15)
如果有长字符串或SQL查询，可以使用Text Blocks：

Java



// 当前代码中的JPQL查询可以保持不变，但如果需要多行字符串，可以使用String query = """    SELECT g FROM Game g     WHERE LOWER(g.name) LIKE LOWER    (CONCAT('%', :keyword, '%'))     OR LOWER(g.description) LIKE     LOWER(CONCAT('%', :keyword,     '%'))    """;
8. 使用Sealed Classes (Java 17)
如果需要限制继承关系，可以使用Sealed Classes：

Java



public sealed class Game permits ActionGame, PuzzleGame {    // ...}non-sealed class ActionGame extends Game {    // ...}final class PuzzleGame extends Game {    // ...}
9. 使用Virtual Threads (Java 21)
虽然在当前代码中没有明显的并发处理需求，但如果需要处理大量并发请求，可以考虑使用Java 21的Virtual Threads：

Java



@Configurationpublic class VirtualThreadConfig {    @Bean    public AsyncTaskExecutor     applicationTaskExecutor() {        return new         VirtualThreadTaskExecutor();    }}
10. 使用Lombok的@Builder和@SuperBuilder
对于需要复杂构建过程的实体，可以使用@Builder：

Java



@Builder@Entitypublic class Game {    // ...}// 使用时Game game = Game.builder()    .name("Game Name")    .developer("Developer")    .build();
11. 使用Spring Boot 3的新特性
确保使用了Spring Boot 3的新特性，如：

使用@Router和@Controller替代部分@RequestMapping
使用新的Observability API
使用新的Docker支持特性
12. 使用JPA 3.1的新特性
可以考虑使用JPA 3.1的一些新特性，如：

更好的JSON支持
改进的Criteria API
这些改进将使代码更加现代化，利用了Java 21、Spring Boot 3、Lombok和JPA的新特性，提高了代码的可读性和维护性。