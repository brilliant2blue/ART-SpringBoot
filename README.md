#ART-SpringBoot
API DOC： http://localhost:10287/doc.html
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
1. main 主模块，启动类和项目配置
2. common 公共工具模块，放置所有模块都会用到的工具（如日志工具，文件读写工具）。
3. user 用户模块，与用户业务相关的实现。
4. vrm 需求工程模块，实现领域概念库相关功能与VRM模型构建。
5. vrmCheck 模型分析模块，实现VRM模型的一致性完整性静态分析。
6. vrmVerify需求工程模块，实现VRM动态模型验证。
7. atg 测试用例自动生成模块，实现ATG相关业务
> 业务模块负责各自模块的功能实现

## 怎么新增模块？
1. 创建新模块，模块的包名形如 `com.nuaa.art.xxx`, 
2. 删除模块目录下的.git文件夹
3. 文件 > 设置 > 版本控制 > 目录映射; 删除模块目录的路径映射，
> 以上操作是为了避免生成多个git仓库
4. 删除模块的主文件。
5. 在ART父模块的pom.xml中的<modules>增加模块名。
6. 复制VRM模块下的pom文件到模块文件中，并修改<artifactId>、<name>、<description>为模块对应的属性。
7. 在main的pom.xml中添加对新模块的<dependency>
