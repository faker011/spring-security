# Spring Security OAuth2 认证授权

#### 1. 基本概念

##### 1.1 什么是认证

​		进入移动互联网时代，大家每天都在刷手机，常用的软件有微信，支付宝，头条等，下面拿微信来举例子说明认证相关的基本概念，再初次使用微信前需要注册微信用户，然后输入账号和密码即可登录微信，输入账号和密码登录微信的过程就是认证。

​		系统为什么要认证/

​		认证是为了保护系统的隐私数据与资源，用户的身份合法方可访问该系统的资源。

​		***认证***：用户认证就是判断一个用户的身份是否合法的过程，用户去访问系统资源时系统要求验证用户的身份信息，身份合法方可继续访问，不合法则拒绝访问。常见的用户身份认证方式有：用户名密码登录，二维码登录，手机短信登录，指纹认证等方式。

##### 1.2 什么是会话

​		用户认证通过后，为了避免用户的每次操作都进行认证可将用户的信息保证在会话中。会话就是系统为了保持当前用户的登陆状态所提供的机制，常见的有基于session方式，基于token方式等。

​		基于session的认证方式如下

 		它的交互流程是，用户认证成功后，在服务端生成用户相关的数据保存在session（当前会话）中，发给客户端的session_id存放到cookie中，这样用户客户端请求时带上session_id就可以验证服务器端是否存在session数据，以此完成用户的合法校验，当用户退出系统或session过期销毁时，客户端的session_id也就无效了

![TIM截图20191103194420](C:\Users\ciaos\Desktop\doc\Oauth2\TIM截图20191103194420.png)

​		基于token方式如下图：

​		它的交互流程时，用户认证成功后，服务端生成一个token发给客户端，客户端可以放到cookie或localStorage等存储中，每次请求时带上token，服务端收到token通过验证后即可确认用户身份。

​		![TIM截图20191103194758](C:\Users\ciaos\Desktop\doc\Oauth2\TIM截图20191103194758.png)

​		基于session的认证方式有servlet规范定制，服务端要存储session信息需要占用内存资源，客户端需要支持cookie；基于token的方式一般不需要服务端存储token，并且不限制客户端的存储方式。如今移动互联网时代更多类型的客户端需要接入系统，系统多是采用前后端分离的架构进行实现，所以基于token的方式更适合。

##### 1.2 什么是授权

​		还拿微信来举例子，微信登录成功后用户即可使用微信的功能，比如，发红包，发朋友圈，添加好友等，没有绑定银行卡的用户是无法发送红包的，保定银行卡的用户才可以发红包，发红包功能，发朋友圈功能都是微信的资源即功能资源，用户拥有发红包功能的权限才可以正常使用发送红包功能，拥有发朋友圈功能的权限才可以使用发朋友圈功能，这个根据用户的权限来控制用户使用资源的过程就是授权。

​		为什么要授权？

​		认证是为了保证用户身份的合法性，授权则是为了更细力度的对隐私数据进行划分，授权是在认证通过后发生的，控制不同的用户能够访问不同的资源。

​		***授权***： 授权是用户认证通过根据用户的权限来控制用户访问资源的过程，拥有资源的访问权限则正常访问，没有权限则拒绝访问。

##### 1.3 授权的数据模型

​		如何进行授权即如何对用户访问资源进行控制，首先需要学习 授权相关的数据模型。

​		授权可简单理解为Who对What进行How操作，包括如下：

​		Who，即主体，主体一般指用户，也可以是程序，需要访问系统中的资源。

​		What，即资源，如系统菜单，页面，按钮，代码方法，系统商品信息，系统订单信息等。系统菜单，页面，按钮，代码方法都属于系统功能资源，对于web系统伟哥功能资源类型和资源实例组成，比如商品信息为资源类型，商品编号为001的商品为资源实例。

​		How，权限/认可，规定了用户对资源的操作许可，权限离开资源没有意义，如用户查询权限，用户添加权限，某个代码方法的调用权限，编号为001的用户的修改权限等，通过权限可知用户对哪些资源都有哪些操作许可。

​		主体、资源、权限关系如下图

![TIM截图20191103201252](C:\Users\ciaos\Desktop\doc\Oauth2\TIM截图20191103201252.png)

主体、资源、权限相关的数据模型如下：

		1. 主体（用户id、账号、密码、...)
  		2. 资源（资源id、资源名称、访问地址、...）
        		3. 权限（权限id、权限标识、权限名称、资源id、...）
            		4. 角色（角色id、角色名称、...）
                		5. 角色和权限关系（角色id、权限id、...）
          		6. 主体（用户）和角色关系（用户id、角色id、...）

主体（用户）、资源、权限关系如下图：

![TIM截图20191103202146](C:\Users\ciaos\Desktop\doc\Oauth2\TIM截图20191103202146.png)

通常企业开发中将资源和权限表合并为一张权限表，如下：

		1. 资源（资源id、资源名称、访问地址、...）
  		2. 权限（权限id、权限标识、权限名称、资源id、...）

   合并为：

	1. 权限（权限id、权限标识、权限名称、资源名称、资源访问地址、...)

修改后的数据模型之间关系如下图：

![TIM截图20191103202726](C:\Users\ciaos\Desktop\doc\Oauth2\TIM截图20191103202726.png)

##### 1.4 RBAC

​		如何实现授权？业界通常基于RBAC实现授权。

###### 1.4.1 基于角色的访问控制

​		RBAC基于角色的访问控制（Role-Based Access Control) 是按角色进行授权，比如：主体的角色为总经理可以查询企业运行报表，查询员工工资等，访问控制流程如下

![TIM图片20191104213805](C:\Users\ciaos\Desktop\doc\Oauth2\TIM图片20191104213805.png)

根据上图中的判断逻辑，授权代码可表示如下：

```java
if(主体.hasRole("总经理角色id")){
    // 查询工资
}
```

###### 1.4.2 基于资源的访问控制

​		RBAC（Resource-Based Access Control） 是按资源（或权限）进行授权，比如：用户必须拥有查询工资权限才可以查询员工工资信息等，访问控制流程如下：

![TIM图片20191104213805](C:\Users\ciaos\Desktop\doc\Oauth2\TIM图片20191104213805.png)

根据上图中的判断逻辑，授权代码可以表示为：

```java
if(主体.hasPermission("查询工资权限标识")){
	// 查询工资
}
```

优点：系统设计时定义好查询工资的权限标识，即使查询工资所需要的角色变化为总经理和部门经理也不需要修改授权代码，可扩展性强。

#### 3. Spring Security 快速上手

##### 3.1 Spring Security介绍

​		Spring Security是一个能够基于Spring的企业应用系统提供声明式的安全访问控制解决方案的安全框架。由于它是Spring生态系统中的一员，因此它伴随着整个Spring生态系统不断修正，升级，在Spring boot项目中加入Spring Security更是十分简单，使用Spring Security减少了为企业系统安全控制编写大量重复代码的工作。

##### 3.2 创建工程

###### 3.2.1 创建maven工程

创建maven工程 security-spring-security，工程结构如下

```xml
		<dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-web</artifactId>
            <version>5.1.4.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-config</artifactId>
            <version>5.1.4.RELEASE</version>
        </dependency>
```

###### 3.2.2 Spring容器配置

```java
@Configuration
@ComponentScan(basePackages = "com.ccfish.security.springmvc",excludeFilters = {@ComponentScan.Filter(type = 	FilterType.ANNOTATION, value = Controller.class)})
public class ApplicationConfig{
    // 在此配置除了Controller的其他bean，比如：数据库连接池、事务管理器、业务bean等
}
				
```

###### 3.2.3 Servlet Context 配置

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.ccfish.security.springmvc",
                includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public InternalResourceViewResolver viewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/").setViewName("login");
    }
}
```

###### 3.2.4 初始化Spring容器

```java
public class SpringApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{ApplicationConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```

##### 3.3 认证

###### 3.3.1 认证页面

​		spring security默认提供认证页面不需要额外开发

###### 3.3.2 安全配置

​		spring security提供了用户名密码登录、退出、会话管理等认证功能，只需要配置即可使用

​		1. 在config包下定义WebSecurityConfig，安全配置的内容包括：用户信息、密码编码器、安全拦截机制

```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // 配置用户信息服务
    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("zhangsan").password("123").authorities("p1").build());
        manager.createUser(User.withUsername("lisi").password("456").authorities("p2").build());
        return manager;
    }

    // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    // 配置拦截
    @Override
    protected void configure(HttpSecurity  httpSecurity) throws Exception{
        httpSecurity.authorizeRequests()
                // 所有/r/**都必须认证 才能通过
                .antMatchers("/r/**").authenticated()
                // 其余可以访问
                .anyRequest().permitAll()
                .and()
                // 允许表单登录
                .formLogin()
                // 自定义成功地址
                .successForwardUrl("/login-success");
    }

}
```

2. 加载 WebSecurityConfig

   修改SpringApplicatioinInitializer的getRootConfigClasses()方法， 添加WebSecurityConfig.class

   ```java
   	@Override
       protected Class<?>[] getRootConfigClasses() {
           return new Class[]{ApplicationConfig.class, WebSecurityConfig.class};
      }
   ```

###### 3.3.2 SrpingSecurity初始化

​		Spring Security初始化，这里有两种情况。

​		· 若当前环境 没有使用Spring或SpringMVC， 则需要将WebSecurityConfig(SpringSecurity配置类)传入超			类，以确保获取配置，并创建spring context

​		· 相反，若当前环境已经使用spring，我们应该在现有的springContext中注册spring security（上一步已经做			将WebSecurityConfig加载至rootContext）此方法可以什么都不做

在init包下定义 SpringSecurityApplicationInitializer

```java
public class SpringSecurityApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    public SpringSecurityApplicationInitializer(){

    }
}
```

###### 3.2.3  默认根路径请求

​		在WebConfig.java中添加默认请求根路径跳转到/login，此url为spring security提供：

修改WebConfig：

```java
	// 默认url根路径跳转到/login，此url为spring security提供
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/").setViewName("redirect:/login");
    }
```

###### 3.2.4 认证成功页面

​		在安全配置中，认证成功将跳转到login-success，代码如下：

```java
	// 配置拦截
    @Override
    protected void configure(HttpSecurity  httpSecurity) throws Exception{
        httpSecurity.authorizeRequests()
                // 所有/r/**都必须认证 才能通过
                .antMatchers("/r/**").authenticated()
                // 其余可以访问
                .anyRequest().permitAll()
                .and()
                // 允许表单登录
                .formLogin()
                // 自定义成功地址
                .successForwardUrl("/login-success");
    }
```

spring security 支持form表单认证，认证成功后跳转到/login-success

在LoginController中定义/login-success:

```java
	@RequestMapping(value="/login-success", produces = {"text/plain;charset=UTF-8"})
    public String loginSuccess(){
        return "登录成功";
    }
```

###### 3.2.5 测试

1. 启动项目：添加maven-configurations

![TIM图片20191105213829](C:\Users\ciaos\Desktop\doc\Oauth2\TIM图片20191105213829.png)

2. 访问http://localhost:8080/security-spring-security/login

3. 登录页面

   ![TIM截图20191105213931](C:\Users\ciaos\Desktop\doc\Oauth2\TIM截图20191105213931.png)

4. 登录成功

##### 3.4 授权

​		实现授权需要对用户的访问进行拦截校验，校验用户的权限是否可以操作指定的资源，Spring Security默认提供授权实现方法

在LoginController中添加 /r/r1 和 /r/r2

```java
	@RequestMapping(value="/r/r1", produces = {"text/plain;charset=UTF-8"})
    public String r1(){
        return "访问资源R1";
    }
    @RequestMapping(value="/r/r2", produces = {"text/plain;charset=UTF-8"})
    public String r2(){
        return "访问资源R2";
    }
    @RequestMapping(value="/s/s1", produces = {"text/plain;charset=UTF-8"})
    public String s1(){
        return "不需要授权资源S1";
    }
```

由于在WebSecurity中配置 /r/** 都需要授权才能访问 所以访问r1 r2时未登录会跳转到登录页面 ；访问 s1 时直接跳转到 s1。

```java
				// 要求访问/r/r1必须有p1权限
                .antMatchers("/r/r1").hasAuthority("p1")
                // 要求访问/r/r2必须有p2权限
                .antMatchers("/r/r2").hasAuthority("p2")
```

##### 3.5 小结

​		通过快速上手，使用Spring Security实现了认证和授权， Spring Security提供了基于账号和密码的认证方式，通过安全配置即可实现请求拦截，授权功能，Spring Security能完成的不仅仅是这些。