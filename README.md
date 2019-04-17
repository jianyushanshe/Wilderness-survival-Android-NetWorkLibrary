
版本号	修改人	时间	备注
5.1	王建明	2019-4-17	

**1.集成步骤**

在gradle中添加引用
```
llprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
	
dependencies {
	     implementation 'com.github.jianyushanshe:Wilderness-survival-Android-NetWorkLibrary:4.0'
	}

```



**2.使用方法**

2.1在Application的onCreat方法中初始化

```
 //初始化ApiBox
        ApiBox.Builder builder = new ApiBox.Builder();
        builder.application(this)
                .debug(BuildConfig.DEBUG)//是否是debug模式
                .connetTimeOut(30000)//连接超时时间
                .readTimeOut(30000)//读取超时时间
                .jsonParseExceptionCode(1003)//json解析异常标识
                .successCode(200)//访问成功标识
                .tokenExpiredCode(406)//token过期，重新登录标识
                .unknownExceptionCode(1000)//未知异常标识
                .build();
```

2.2：network包中创建请求的interface类

例如：

```
public interface AccountApi {
   //登录
    @GET(".")
    Flowable<UserEntity> login(@QueryMap TreeMap<String, String> map);
}
```

2.3：network包中创建一个类，实例化2.2中创建的interface。

例如：

```
public class AccountNetwork {
  
    public static AccountNetwork accountNetwork;
    private static AccountApi accountApi;

    private AccountNetwork() {
    }
	//单例
    public static synchronized AccountNetwork get() {
        if (accountNetwork == null) {
            accountNetwork = new AccountNetwork();
        }
        if (accountApi == null) {
          accountApi = ApiBox.getInstance().createService(AccountApi.class, HttpUrl.Base_Url);
        }
        return accountNetwork;
    }

    /**
     * @功能：登录
     * @param：
     * @return：
     */
    public Flowable<UserEntity> login(String userName, String userPassword) {
        // 添加参数到集合
        TreeMap<String, String> tMap = new TreeMap<>();
        tMap.put("username", userName);
        tMap.put("pwd", userPassword);
        Flowable<UserEntity> observable = accountApi.login(tMap);
        return observable;
    }

}
```

2.4 repository包中创建一个类，进行数据请求和处理

例如：

```
Public class LoginRepository extends BaseRepository<LoginContract.Presenter, LoginContract.View> implements LoginContract.Repository {
    /**
     * LoginRepository构造方法
     *
     * @param presenter
     */
    public LoginRepository(LoginContract.Presenter presenter, LoginContract.View view, Activity context) {
        super(presenter, view, context);
    }
    /**
     * 登录操作，异步网络请求，请求成功将返回数据交给Presenter做业务处理
     */
    @Override
    public void userLogin(String name, String pwd) {
        //一般弹的失败情况已处理，若需改写重写 onUserError 并去掉super(xx).
     BaseSubscriber<ResBase> subscriber = new BaseSubscriber<ResBase>(context, view) {
            @Override
            protected void onUserSuccess(ResBase resBase) {
            //请求成功返回的数据
                UserEntity userEntity = new UserEntity();
                userEntity.state = 1;
                userEntity.userName = "用户名";
                userEntity.userPassword = "用户密码";
                presenter.upData(userEntity);
            }

            @Override
            protected void onUserError(CommonException ex) {
                super.onUserError(ex);
                //请求错误，需要用户处理的异常
            }

            @Override
            protected void onUnifiedError(CommonException ex) {
                super.onUnifiedError(ex);
                //请求错误，系统级别异常
            }
        };
        //一个请求
        //RxUtils.<UserEntity>getScheduler(true, view)参数1，是否显示loading；参数2，显示loading的view
        Disposable disposable = AccountNetwork.get()
                .login(name, pwd)
                .compose(RxUtils.<UserEntity>getScheduler(true, view))
                .subscribeWith(subscriber);
        //添加到订阅集合中,在activity或fragment,onDestroy时取消
        rxManage.add(disposable);
    }
}
```

2.5 在BaseActivity中，继承ILoading 接口，重写reLogin重新登录的方法、showLoading加载进度显示方法和dismissLoading隐藏进度显示方法

```
    /**
     * 重新登录，网络库里面会调用
     *
     * @param context
     * @param s
     */
    @Override
    public void reLogin(Context context, String s) {
        //重新登录需要做的操作
    }

```

```
  @Override
    public void showLoading() {
   //显示加载的进度
    }
```

```
  @Override
    public void dismissLoading() {
       //隐藏进度显示
    }
```

