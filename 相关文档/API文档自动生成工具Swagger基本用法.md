# SpringDoc/Swagger3 简单用法
项目中使用SpringDoc来生成Api的文档，用于前端调试接口
1. 查看API： http://localhost:10287/swagger-ui/index.html
2. 使用Swagger的注解，从而可以在执行时生成APi文档。
常用注解：
    1. `@Tag(name = "")`， 用在Controller类上，生成这个Controller的标签
   2. `@Operation(summary = "",description = "")`， 用在Controller类的方法上，用来描述API的信息，summary 描述方法简要功能，description进行详细描述。
   3. `@Parameter(name = "" ,description = "" )`, 用在Controller类的方法上，用来描述API的信息，name 为方法在url中的参数名， description 参数的描述。
