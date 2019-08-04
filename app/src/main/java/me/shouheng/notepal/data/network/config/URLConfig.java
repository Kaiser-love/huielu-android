package me.shouheng.notepal.data.network.config;

public interface URLConfig {
    String baidu_url = "s";
    String INIT_URL = "appInit";
    String login_token_url = "获取新token的地址";
    String ZIMG_UPLOAD_URL = "https://huielu.com/pic/upload";
    //  album
    String GET_DIRECTORY_LIST = "getdirectorylist";
    String CREATE_DIRECTORY = "createdirectory";
    String UPDATE_DIRECTORY = "updatedirectory";
    String DELETE_DIRECTORY = "deletedirectory";
    String GET_DIRECTORY_BY_NAME = "getDirectoryByName";
    //picture
    String GET_PICTURE_LIST = "getpicturelist";

    String GET_PPT_LIST = "getPPTList";
    String SAVE_PPT = "saveppt";

    //    String  = "uploadpicture";
    public static interface UPLOAD_PICTURE {
        String url = "uploadpictureByName";
        String DIR_NAME = "dirName";
        String PATH = "path";
    }

    String DELETE_PICTURE = "deletepicture";
    String UPDATE_PICTURE = "updatepicture";

    //user

    /**
     * 使用验证码登录
     */
    public static interface LOGIN_WITH_AUTHCODE {
        String URL = "loginwithauthcode";
        String PHONE = "phone";
        String AUTHCODE = "authcode";
    }

    /**
     * 登录时获取头像
     */
    public static interface REQUEST_HEAD {
        String URL = "requesthead";
        String USERNAME = "username";
        String EMAIL = "email";
        String PHONE = "phone";
    }

    /**
     * 检查是否存在用户名
     */
    public static interface CHECK_USERNAME {
        String URL = "checkusername";
        String USERNAME = "username";
    }

    /**
     * 注册
     */
    public static interface REGISTER {
        String URL = "register";
        String PASSWORD = "password";
        String AUTHCODE = "authcode";
        String PHONE = "phone";
    }

    /**
     * 发送验证码
     */
    public static interface REQUEST_AUTHCODE {
        String URL = "requestauthcode";
        String PHONE = "phone";
    }

    /**
     * 更新密码
     */
    public static interface UPDATE_PASSWORD {
        String URL = "updatepassword";
        String UID = "uid";
        String PASSWORD = "password";
    }

    /**
     * 普通登录
     */
    public static interface LOGIN {
        String URL = "login";
        String USERNAME = "username";
        String PASSWORD = "password";
        String EMAIL = "email";
        String PHONE = "phone";
    }

    interface AUTH {
        String URL = "/auth/token";
        String ACCOUNT = "account";
        String PASSWORD = "password";
    }

    public static interface LOGIN_WITH_TOCKEN {
        String URL = "login";
        String USERNAME = "username";
        String TOCKEN = "TOCKEN";
    }

    public static interface LOGOUT {
        String URL = "logout";

    }

    interface UPLOADSINGLEFILE {
        String URL = "/pptOrpdf/upload";
        String FILES = "files";
        String MODE = "mode";
        String NAME = "name";
    }

    interface UPLOADFILES {
        String URL = "/pptOrpdf/upload";
        String FILES = "files";
        String MODE = "mode";
        String NAME = "name";
    }

    interface DOWNLOADFILE {
        String URL = "/pptOrpdf/download";
        String ID = "id";
        String MODE = "mode";
        String NAME = "name";
    }
}