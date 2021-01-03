# Bean 的加载

## Bean的解析

Bean的解析主要是对xml文件的解析，对xml文件的解析分为默认标签的解析，自定义标签的解析，默认标签的解析中包括：“import”，“alias”，“bean”以及“beans”标签

整个解析的入口在：

> ```
> DefaultBeanDefinitionDocumentReader.doRegisterBeanDefinitions() -> DefaultBeanDefinitionDocumentReader.parseBeanDefinitions()
> ```

### 默认标签的解析

- 默认标签的解析流程入口为：

  > DefaultBeanDefinitionDocumentReader.parseBeanDefinitions()   ->
  >
  >  DefaultBeanDefinitionDocumentReader.parseDefaultElement()

#### "bean"标签

标签的解析过程大体上都差不多，就挑选相对比较复杂的"bean"标签过程分析

```java
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		//如果解析成功，则返回BeanDefinitionHolder对象，BeanDefinitionHolder对象为name和alias[]的BeanDefinition对象
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			//自定义标签处理
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				// 注册
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			// 发出响应事件，通知相关的监听器，已完成该 Bean 标签的解析
			// ？ 目前有哪些监听器，分别监听之后做了什么工作
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}
```

- **BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele)**

  - ***todo***

- **delegate.decorateBeanDefinitionIfRequired(ele, bdHolder)**

  - 这个方法的作用是：如果有需要的话，就对beanDefinition进行装饰，适用场景是(在默认标签中的子元素使用了自定义的配置)：

    ```xml
    <bean id="test" class="test.MyClass">
        <mybean:user username="aaa" />
    </bean>
    ```

  - 这段解析为什么不放在自定义标签中解析呢？
    - 这个自定义标签并不是以bean的形式出现的
    - 区分是否是自定义的标签的逻辑是通过判断namespaceURI的方式判断的

### 自定义标签的解析











## Bean的加载