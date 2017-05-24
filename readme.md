## 电影票购票系统后端接口

### 全局信息

大多数返回接口类似

```
{
  success:successCode
  value:...
}
```

其中successCode为返回状态码，目前用到的如下

| 状态码 | 表示|
|------|------|
| 2000| 操作成功|
| 4003| 权限不足|
| 5000| 服务器错误或者数据库操作错误 |


## 用户系统

**注意服务端用到了cookie,请在客户端正确发送接收cookie**

用户系统的所有接口为**URL+"/user/"+具体接口**

#### `/captcha` 得到图形验证码接口

请求方式：get 

返回：返回一个svg格式的验证码图

---

#### `/captchatest/:code` 测试svg验证码是否正确

发送请求前请验证:

* 图形验证码位数正确(4位)

请求方式：get

返回：   
正确返回 `{success:2000,value:{result: true}}`  
错误返回 `{success:2000,value:{result: false}}`

success不为2000的时候为出错

---

#### `/finduser/:phone/:name` 用于注册的时候查询用户信息防止用户输入重复

发送请求前请验证：

* 用户名、手机号、密码不为空并且手机号符合手机号格式
* 用户名不能和手机号相似`"^[0-9]{11}$"`

请求方式: get

返回：  
如果有这个用户`{success:2000,value:{result:true}}`  
如果没这个用户`{success:2000,value:{result:false}}`

---



#### `/send/:captcha/:phone` 发送短信验证码

发送请求前请验证：

* 已经通过以上请求验证(手机号和用户名已经填写并符合格式,都不重复、svg验证码正确)
* 第一次发送或者间隔时间已经超过30s

请求方式: get

返回：   
成功的话返回 `{success:2000}`   
失败的话返回 `{success:errCode,value:"..."}`(errCode为4003或者5000)

---

#### `/insert/:verify'` 新用户插入,用于注册

发送请求前请验证：

* 已经通过以上所有验证
* vertify位数正确(6位)

请求方式: post

请求参数：

```
{
  phone:xxx
  name:xxx
  password:xxx
}
```

返回:

正确情况

```
{result:2000,
 value: {id:,name:,phone:,registertime:}}
```

---

#### `/login/:vertify` 用户登录

发送请求前请验证：

* 已经发送短信验证码并且vertify位数正确(6位)

请求方式： post

请求参数

```
{
	phone_name:"XXX"
	password:XXX
}
```

*备注：用户输入用户名或者手机号任意一项都可以*

返回:

正确情况

```
{result:2000,
 value: {id:,name:,phone:,registertime:}}
```

其他情况错误原因会在value中给出


---

#### `/userLogout` 用户注销登录 这个看情况，可能用不到