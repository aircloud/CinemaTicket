>注意:目前接口已经全面换成https,原来的http失效

preURL: https://c.10000h.top/

例如: https://c.10000h.top/user/captcha

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

| 状态码  | 表示             |
| ---- | -------------- |
| 2000 | 操作成功           |
| 4003 | 权限不足           |
| 5000 | 服务器错误或者数据库操作错误 |


## 用户系统

**注意服务端用到了cookie,请在客户端正确发送接收cookie**

用户系统的所有接口为**URL+"/user/"+具体接口**

#### `/captcha` 得到图形验证码接口

注意:若需要刷新验证码,在后面加一个随机查询字符即可，比如`/captcha?rando=i2932891291928`

请求方式：get 

返回：返回一个svg格式的验证码图

---

#### `/captchatest/:captcha` 测试svg验证码是否正确

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
如果有这个用户并且手机号被占用`{success:2000,value:{result:true,type:"phone"}}`    
如果有这个用户并且用户名被占用`{success:2000,value:{result:true,type:"name"}}`    
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

#### `/insert/:verify` 新用户插入,用于注册

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
 user: {id:,name:,phone:,registertime:}}
```

---

#### `/login/:captcha` 用户登录

发送请求前请验证：

* 图形验证码正确

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
 user: {id:,name:,phone:,registertime:}}
```

其他情况错误原因会在value中给出

---

#### `/userLogout` 用户注销登录 这个看情况，可能用不到


## 电影票系统(测试阶段)

**前缀为`/main/`**

>电影票系统的原则是,能爬数据我尽量用真数据,如果是真数据的话肯定有一个线程在不断更新数据,实在爬不到真数据我就用假数据,但是保证字段一样。

#### `/cinemas` 影院信息

请求方式：GET


会返回一些影城的名字和地址，还有起始价

```
[
"success":2000,
"value":[{"id":1,"name":"横店电影城(杭州下沙店)","address":"杭州市江干区下沙宝龙商业中心三号楼3F-001","beginprice":17,"nm_cinema":"8416"},......]
]
```

#### `/movies` 返回电影信息

这个如果图片显示不出来应该就是有图片防盗链，请告知我我写一个破解机制。

请求方式：GET


```
{"success":2000,"value":[{"id":1,"name":"新木乃伊","abstract":"木乃伊归来","score":6.5,"type":"冒险,动作,恐怖","duration":"106分钟","showtime":"2017-6-9","photo":"https://gss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/movie/pic/item/a5c27d1ed21b0ef4c3ff5f50d7c451da81cb3ead.jpg","nm_movieid":"63068"},......]}
```

#### `/recommend` 返回推荐电影信息

请求方式：GET


返回目前在上映的电影的推荐信息，根据评分来推荐，数据格式和上面的电影信息格式一样。

目前暂定五条，这个数目可以协商。

```
{"success":2000,"value":[{"id":6,"name":"摔跤吧！爸爸","abstract":"谁说女子不如男","score":9.6,"type":"动作,家庭,喜剧","duration":"140分钟","showtime":"2017-5-5","photo":"https://gss0.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/movie/pic/item/dc54564e9258d109afede3a3db58ccbf6c814d90.jpg","nm_movieid":"62983"},......]}
```


 然后点击某一个电影出现影院选择界面，这个影院选择界面的列表和影院信息的影院一样(我们不做定位系统，我就把杭州所有影院返回) 

#### `/schedule/:cinemaid/:movieid` 返回排片页面

请求方式：GET


会返回排片时间、几号厅、已售出多少等

注意传给我的是两个id,这个id信息前面几个接口部分我有提供

```
{"success":2000,"value":[{"id":92,"begintime":"12:00","endtime":"13:46散场","dimension":"3D","hall":"5号厅","price":"￥41","surplus":80,"cinemaid":"8080","movieid":"63068"},......]}
```

#### `/getorderseats/:arrangeid` 获取已经被占用的座位的编号

请求方式：GET

>实际上这个arrange就是schedule的意思,一般都和排片表的字段有关

这里为了前后端解析方便，我觉得你不要传给我行号和列号了,直接传给我一个转化成一维之后的id，这样我们都比较方便。

我会把已经占用的座位用对象数组的方式给你：

```
{"success":2000,"value":[{"seatid":0},{"seatid":20},{"seatid":30},......]}
```

#### `/order/:arrangeid/:userid/:seatid` 购票，返回随机字符串

请求方式：POST

```
{
      success:2000,
      value:/*这个值是你需要转化成二维码的随机字符串，20位左右*/
}
```

然后这个二维码是要保存的，到时候可以拿出来扫一扫，也就是说个人中心应该有一个“我购买的票”这样一个栏目。

#### `/searchorder/:ordernumber` 供小程序使用的查询接口 

请求方式：POST

如果失败:

```
{
       success:3000,
       value:"没有查询到相关信息"
}
```
如果成功，一个例子：

```
{"success":2000,"value":{"id":92,"begintime":"12:00","endtime":"13:46散场","dimension":"3D","hall":"5号厅","price":"￥41","surplus":79,"cinemaid":"8080","movieid":"63068"}}
```
#### `/loginunsafe` 不安全的用户登陆接口,供小程序使用

请求方式：POST

具体使用方式和上面`/login`一样，只是不用发送图形验证码了