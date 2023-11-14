使用示例：
```java
@Async("AsyncTask")
public void creatlocal(Integer systemId)throws IOException{
    try{
        webSocketService.sendMsg(SocketMessage.asText("开始创建模型"));
        LogUtils.info("开始创建模型");
        Thread.sleep(1000);
        VariableRealationModel model=(VariableRealationModel)modelXmlCreate.createModel(systemId);
        LogUtils.info("模型创建结束");
        webSocketService.sendMsg(SocketMessage.asText("模型创建结束"));
        webSocketService.sendMsg(SocketMessage.asText(model.getDate()));
        webSocketService.sendMsg(SocketMessage.asObject("vrm-model",model));
    }catch(InterruptedException e){
        LogUtils.error(e.getMessage());
    }
}
```

1. 如何定义异步线程？
    加上注解`@Async("AsyncTask")`, 然后正常在controller中使用就行。注意，为避免麻烦，异步函数的返回值应为空，然后通过websockt返回异步消息。
2. 如何使用websocket？
   1. 在需要使用为文件中引入
    ```java
    @Resource
    WebSocketService webSocketService;
    ```
    2. 发送消息
   使用提供的sendMsg方法，这里提供了三种重载方法, 一般直接使用第三种即可。
   ```java
    public void sendMsg(WebSocketSession session, String text) ;
    public void sendMsg(WebSocketSession session, TextMessage msg);
    public <T> void sendMsg(T msg) ;
   ```
   3. 由于前端的设置，后端发送消息的需要进行封装。主要分成两大类：msg消息和obj对象传输。对应封装结构体如下，并且提供了asText和asObject两类方法。
   ```java
    public class SocketMessage<T> {
    String messageType;
    String dataType;
    T data;

    public static TextMessage asText(String data);

    public static <T> TextMessage asObject(String type, T data);
   ```
3. 前端的消息处理：
   1. 前端的api文件夹中Socket文件对Socket消息类型进行统一管理。
   2. 前端的common文件夹SocketMsg文件是对接收到的wensocket消息的处理逻辑。

特别的，前端通过websocket实现了进度条的显示功能。对于约定为obj类型的消息，如果传递为了msg类型，那么前端在进度条中显示，直到前端页面切换或者接收到空消息。