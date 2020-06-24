# AZERO IoT Client SDK（Java）

AZERO IoT Client SDK（JAVA）开发人员可以使用java语言配合MQTT协议访问AZERO IoT平台。

由于IoT技能、IoT设备对于Azero IoT云来说，都属于Client端，因此借助此SDK，开发者可以开发Azero IoT技能，也可以开发IoT设备上的应用程序。

## 概览

本文档提供了有关安装和配置Java的AZERO IoT Client SDK的说明。它还包括一些示例，这些示例演示了如何连接MQTT服务，并进行发布/订阅操作。

### MQTT连接类型

- 基于X.509证书的双向身份验证的MQTT（基于TLS 1.2）

对于基于TLS的MQTT（端口8883），身份验证需要有效的证书和私钥。

### 设备影子

设备影子表示物理设备或设备的云副本。其表现形式为一个Json文档，我们称其为设备影子文档。设备影子文档记录着IoT设备的最新状态，以及控制者期望设备是什么状态等信息。即使设备离线，应用程序仍可访问其设备影子文档。当设备重新联机时，设备影子会将增量通知到设备（设备在脱机时看不到）。具体详情请参考:[设备影子文档结构介绍](https://doc-iot-azero.soundai.cn/docs/deviceShadow/%E8%AE%BE%E5%A4%87%E5%BD%B1%E5%AD%90%E6%96%87%E6%A1%A3%E7%BB%93%E6%9E%84%E4%BB%8B%E7%BB%8D.html)

## 安装SDK

### 最低要求

要使用SDK，您将需要Java 1.7+。

### 从GitHub源码构建SDK

您可以从GitHub托管的源代码构建SDK及其示例应用程序。

```sh
$ git clone https://github.com/azero/azero-iot-device-sdk-java.git
$ cd azero-iot-device-sdk-java
$ mvn clean install
```

## 使用SDK

以下各节提供了一些使用SDK通过MQTT访问AZERO IoT服务的基本示例。

- ### 初始化客户端

要访问AZERO IoT服务，您必须初始化`AZEROIotMqttClient`。需要有效的客户端端点和客户端ID来建立连接。

- 使用MQTT（通过TLS 1.2）初始化客户端：对于此MQTT连接类型（端口8883），AZERO IoT服务需要TLS相互认证，因此需要有效的客户端证书（X.509）和RSA密钥。您可以使用 [AZERO IoT控制台](https://azero.soundai.com/iot/)。对于SDK，仅需要证书文件和私钥文件。

```java
String clientEndpoint = "<endpoint>";       // 替换成你的终端节点(IoT控制台个人头像处查看)
String clientId = "<unique client id>";     // 替换成你的唯一客户端ID
String certificateFile = "<certificate file>";  // 替换成IoT控制台下载的证书文件
String privateKeyFile = "<private key file>";   // 替换成IoT控制台下载的私钥文件
String thingName = "<thing name>";// 替换成你的设备名称
int tlsPort = 8883; //mqtt 端口
String accountPrefix = "<accountPrefix>"; // 替换成你的账户前缀(终端节点的前缀)
```

### 发布和订阅

初始化并连接客户端后，您可以发布消息并订阅主题。

使用阻塞API发布消息：

```java
String topic = "my/own/topic";
String payload = "any payload";

client.publish(topic, AZEROIotQos.QOS0, payload);
```

要使用非阻塞API发布消息，请执行以下操作：

```java
public class MyMessage extends AZEROIotMessage {
    public MyMessage(String topic, AZEROIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
        // called when message publishing succeeded
    }

    @Override
    public void onFailure() {
        // called when message publishing failed
    }

    @Override
    public void onTimeout() {
        // called when message publishing timed out
    }
}

String topic = "my/own/topic";
AZEROIotQos qos = AZEROIotQos.QOS0;
String payload = "any payload";
long timeout = 3000;                    // milliseconds

MyMessage message = new MyMessage(topic, qos, payload);
client.publish(message, timeout);
```

订阅主题：

```java
public class MyTopic extends AZEROIotTopic {
    public MyTopic(String topic, AZEROIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AZEROIotMessage message) {
        // called when a message is received
    }
}

String topicName = "my/own/topic";
AZEROIotQos qos = AZEROIotQos.QOS0;

MyTopic topic = new MyTopic(topicName, qos);
client.subscribe(topic);
```

### Shadow API

Shadow Service 提供REST形式的API供调用，其作用主要方便开发IoT技能的开发者，在编写技能代码时使用。具体参考：[IoT开放平台文档中心](https://doc-iot-azero.soundai.cn/docs/deviceShadow/%E8%AE%BE%E5%A4%87%E5%BD%B1%E5%AD%90%E6%9C%8D%E5%8A%A1%E6%94%AF%E6%8C%81%E7%9A%84RESTful%20API.html)

## 使用示例

- SDK包含示例应用程序。其在azero-iot-client-sdk-java-samples文件夹下。

- 发布/订阅示例：该示例由两个发布者组成，它们每秒将一个消息发布到一个主题。订阅同一主题的一个订户接收并打印消息。

### 示例应用程序的参数

- 要运行示例，您还需要通过命令行提供以下参数：
  - clientEndpoint：客户端端点，形式为 `<prefix>.iot-fat.bj.soundai.cn`
  - clientId：客户端ID
  - thingName：设备名称
  - certificateFile：基于X.509的证书文件（在AZERO IoT开放平台创建证书获取）
  - privateKeyFile：私钥文件（在AZERO IoT开放平台创建证书获取）
  - accountPrefix：账户前缀，是客户端端点的前缀

## License

[Apache许可2.0版](http://www.apache.org/licenses/LICENSE-2.0)