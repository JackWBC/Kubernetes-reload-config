# Kubernetes-reload-config
Spring-Cloud-Kubernetes 读取Kubernetes ConfigMap以及热加载demo

记得在所在的namespace给相应的ServiceAccount添加以下权限  
资源 "configmaps", "pods", "services", "endpoints", "secrets"  
操作 "get", "list", "watch"  

## 使用 ConfigMap
Spring Cloud Kubernetes Config 项目可以在启动时加载Kubernetes ConfigMap, 同时也支持热加载  
 
#### 在项目中使用ConfigMap的配置如下
```
spring:  
   application:  
     name: cloud-k8s-app  
   cloud:  
     kubernetes:  
       config:  
         name: default-name  
         namespace: default-namespace # 若不设置namespace 则会在项目运行的namespace查找ConfigMap
         sources:  
          # Spring Cloud Kubernetes 在命名空间default-namespace 下查找name为c1的ConfigMap
          - name: c1  
          # Spring Cloud Kubernetes looks up a ConfigMap named default-name in whatever namespace n2  
          - namespace: n2  
          # Spring Cloud Kubernetes looks up a ConfigMap named c3 in namespace n3  
          - namespace: n3  
            name: c3  
```
#### ConfigMap的两种基础使用方式
```
kind: ConfigMap
apiVersion: v1
metadata:
  name: demo
data:
  pool.size.core: 1
  pool.size.max: 16
```
```
kind: ConfigMap
apiVersion: v1
metadata:
  name: demo
data:
  application.yaml: |-   # 使用custom-name.yaml同样可行
    pool:
      size:
        core: 1
        max:16
```
#### ConfigMap配置profile
若配置多个profile active, 则最后配置的生效
```
kind: ConfigMap
apiVersion: v1
metadata:
  name: demo
data:
  application.yml: |-
    greeting:
      message: Say Hello to the World
    farewell:
      message: Say Goodbye
    ---
    spring:
      profiles: development
    greeting:
      message: Say Hello to the Developers
    farewell:
      message: Say Goodbye to the Developers
    ---
    spring:
      profiles: production
    greeting:
      message: Say Hello to the Ops
```
#### 通过配置 SPRING_PROFILES_ACTIVE 环境变量通知Spring Boot使用哪个profile
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment-name
  labels:
    app: deployment-name
spec:
  replicas: 1
  selector:
    matchLabels:
      app: deployment-name
  template:
    metadata:
      labels:
        app: deployment-name
	   spec:
		  containers:
		  - name: container-name
		    image: your-image
		    env:
		    - name: SPRING_PROFILES_ACTIVE
			     value: "development"
```
#### 可以将ConfigMap挂载到 Spring Cloud Kubernetes 应用所在的Pod
Spring Cloud Kubernetes可以通过读取文件系统来获取ConfigMap配置  
可以通过spring.cloud.kubernetes.config.paths配置, 多个用","隔开  

#### ConfigMap相关配置
| Name      | Type     | Default     | Description     |
| ---------- | :-----------:  | :-----------: | :-----------: |
| spring.cloud.kubernetes.config.enabled | Boolean  | true  | Enable ConfigMaps PropertySource |
| spring.cloud.kubernetes.config.name | String  | ${spring.application.name}  | Sets the name of ConfigMap to look up |
| spring.cloud.kubernetes.config.namespace | String  | Client namespace  | Sets the Kubernetes namespace where to lookup |
| spring.cloud.kubernetes.config.paths | List  | null  | Sets the paths where ConfigMap instances are mounted |
| spring.cloud.kubernetes.config.enableApi | Boolean  | true  | Enable or disable consuming ConfigMap instances through APIs |


## 使用Secrets
Kubernetes使用Secrets来存储敏感数据, 例如密码, OAuth tokens等等  

#### 案例
假设我们有一个名称为demo的Spring Boot项目, 并且使用属性读取数据库配置  
创建Kubernetes Secret
```
kubectl create secret generic db-secret --from-literal=username=user --from-literal=password=p455w0rd
```
查看db-secret Secret资源对象 (数据Base64编码)
```
apiVersion: v1
data:
  password: cDQ1NXcwcmQ=
  username: dXNlcg==
kind: Secret
metadata:
  creationTimestamp: 2017-07-04T09:15:57Z
  name: db-secret
  namespace: default
  resourceVersion: "357496"
  selfLink: /api/v1/namespaces/default/secrets/db-secret
  uid: 63c89263-6099-11e7-b3da-76d6186905a8
type: Opaque
```
Deployment使用上述配置  
```
apiVersion: v1
kind: Deployment
metadata:
  name: ${project.artifactId}
spec:
   template:
     spec:
       containers:
         - env:
            - name: DB_USERNAME
              valueFrom:
                 secretKeyRef:
                   name: db-secret
                   key: username
            - name: DB_PASSWORD
              valueFrom:
                 secretKeyRef:
                   name: db-secret
                   key: password
```
#### spring.cloud.kubernetes.secrets.sources配置多个Secret
配置方式与ConfigMap相同  
```
spring:
  application:
    name: cloud-k8s-app
  cloud:
    kubernetes:
      secrets:
        name: default-name
        namespace: default-namespace
        sources:
         # Spring Cloud Kubernetes looks up a Secret named s1 in namespace default-namespace
         - name: s1
         # Spring Cloud Kubernetes looks up a Secret named default-name in whatever namespace n2
         - namespace: n2
         # Spring Cloud Kubernetes looks up a Secret named s3 in namespace n3
         - namespace: n3
           name: s3
```
#### Secret相关配置
| Name      | Type     | Default     | Description     |
| ---------- | :-----------:  | :-----------: | :-----------: |
| spring.cloud.kubernetes.secrets.enabled | Boolean  | true  | Enable Secrets PropertySource |
| spring.cloud.kubernetes.secrets.name | String  | ${spring.application.name}  | Sets the name of the secret to look up |
| spring.cloud.kubernetes.secrets.namespace | String  | Client namespace  | Sets the Kubernetes namespace where to look up |
| spring.cloud.kubernetes.secrets.labels | Map  | null  | Sets the labels used to lookup secrets |
| spring.cloud.kubernetes.secrets.paths | List  | null  | Sets the paths where secrets are mounted |
| spring.cloud.kubernetes.secrets.enableApi | Boolean  | false  | Enables or disables consuming secrets through APIs |

## PropertySource Reload
Spring Cloud Kubernetes可以使相关联的ConfigMap和Secret改变时, 应用可以热加载  
默认情况下, 这项功能是关闭的, 可以通过配置spring.cloud.kubernetes.reload.enabled=true开启  

#### spring.cloud.kubernetes.reload.strategy
热加载策略
###### refresh
只有配置Bean添加 @ConfigurationProperties 或 @RefreshScope 才会热加载
```
@Configuration
@ConfigurationProperties(prefix = "bean")
public class MyConfig {

    private String message = "a message that can be changed live";

    // getter and setters

}
```
###### restart_context
整个Spring ApplicationContext重启, Beans重新生成  
###### shutdown
Spring ApplicationContext 停止, 重启容器  
确保: 所有的非守护线程都绑定到ApplicationContext上, Kubernetes使用了副本策略重启容器  

#### reload相关配置
| Name      | Type     | Default     | Description     |
| ---------- | :-----------:  | :-----------: | :-----------: |
| spring.cloud.kubernetes.reload.enabled | Boolean | flase | Enables monitoring of property sources and configuration reload |
| spring.cloud.kubernetes.reload.monitoring-config-maps | Boolean  | true | Allow monitoring changes in config maps |
| spring.cloud.kubernetes.reload.monitoring-secrets | Boolean | false | Allow monitoring changes in secrets |
| spring.cloud.kubernetes.reload.strategy | Enum | refresh | The strategy to use when firing a reload (refresh, restart_context, or shutdown) |
| spring.cloud.kubernetes.reload.mode | Enum | event | Specifies how to listen for changes in property sources (event or polling) |
| spring.cloud.kubernetes.reload.period | Duration | 15s | The period for verifying changes when using the polling strategy |
  
Notes: * You should not use properties under spring.cloud.kubernetes.reload in config maps or secrets. Changing such properties at runtime may lead to unexpected results. * Deleting a property or the whole config map does not restore the original state of the beans when you use the refresh level.















