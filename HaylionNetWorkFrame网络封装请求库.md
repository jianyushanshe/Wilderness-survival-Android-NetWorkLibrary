
版本号	修改人	时间	备注
1.0.0	王建明	2018-11-12	

**1.集成步骤**

1.1：在工程的buidlgradle中添加

```
allprojects {
    repositories {
       ……
       maven { url 'https://jitpack.io' }
    }
}
```

1.2：在app的buildgradle中添加

```
dependencies {
……
    implementation 'com.github.jianyushanshe:HaylionNewWorkFrame:1.1'
}
```


**2.使用方法**

2.1：network包中创建请求的interface类

例如：

```
public interface AccountApi {
   //登录
    @GET(".")
    Flowable<UserEntity> login(@QueryMap TreeMap<String, String> map);
}
```

2.2：network包中创建一个类，实例化2.1中创建的interface。

例如：

```
public class AccountNetwork {
    public final static String PARAMS_MODULE = "module";//
    public final static String PARAMS_SERVICE = "service";//
    public final static String PARAMS_METHOD = "method";//

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
        tMap.put(PARAMS_MODULE, "imeb");
        tMap.put(PARAMS_SERVICE, "BaseInfo");
        tMap.put(PARAMS_METHOD, "login");
        tMap.put("username", userName);
        tMap.put("pwd", userPassword);
        Flowable<UserEntity> observable = accountApi.login(tMap);
        return observable;
    }

}
```

2.3 repository包中创建一个类，进行数据请求和处理

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
                UserEntity userEntity = new UserEntity();
                userEntity.state = 1;
                userEntity.userName = "用户名";
                userEntity.userPassword = "用户密码";
                presenter.upData(userEntity);
            }

            @Override
            protected void onUserError(CommonException ex) {
                super.onUserError(ex);
            }

            @Override
            protected void onUnifiedError(CommonException ex) {
                super.onUnifiedError(ex);
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

