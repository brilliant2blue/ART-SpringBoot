#ART-SpringBoot
API DOC： http://localhost:10287/swagger-ui/index.html
## 包名规范
1. 项目基本包：com.nuaa.art.{模块名}
2. config：配置类
3. common：公共类，定义常量类，组件
4. entity: 数据库相关的实体类
5. model:业务数据模型类(参数模型，数据传输模型等）
6. controller:控制层接口
7. service: 服务层，复杂的数据库操作，业务逻辑等。包含高层DAO服务与低层controller服务
8. mapper：数据库, 简单的查询，要套在service中执行

## 模块功能
1. main 主模块，项目配置与controller实现
2. common 公共模块，放置所有模块都会用到的组件常量等
3. user 用户模块，与用户业务相关的实现
4. vrm 需求工程模块，实现VRM相关业务
5. atg 测试用例自动生成模块，实现ATG相关业务
> 业务模块负责各自模块的service层及更底层的实现
