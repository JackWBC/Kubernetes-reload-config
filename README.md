# Kubernetes-reload-config
Spring-Cloud-Kubernetes 读取Kubernetes ConfigMap以及热加载demo

## 使用 ConfigMap
 Spring Cloud Kubernetes Config 项目可以在启动时加载Kubernetes ConfigMap, 同时也支持热加载  
 
 #### 在项目中使用ConfigMap的配置如下
 `
 spring:  
    application:  
      name: cloud-k8s-app  
    cloud:  
      kubernetes:  
        config:  
          name: default-name  
          namespace: default-namespace  
          sources:  
           # Spring Cloud Kubernetes looks up a ConfigMap named c1 in namespace default-namespace  
           - name: c1  
           # Spring Cloud Kubernetes looks up a ConfigMap named default-name in whatever namespace n2  
           - namespace: n2  
           # Spring Cloud Kubernetes looks up a ConfigMap named c3 in namespace n3  
           - namespace: n3  
             name: c3  
 `