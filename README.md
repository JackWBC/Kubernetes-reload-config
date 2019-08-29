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





















