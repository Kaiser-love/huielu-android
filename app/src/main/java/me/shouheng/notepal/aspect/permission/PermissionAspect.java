package me.shouheng.notepal.aspect.permission;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.shouheng.commons.utils.PermissionUtils;
import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.album.base.IPresenter;
import me.shouheng.notepal.fragment.album.base.IView;

/**
 * Created by yuyidong on 2017/2/4.
 */
@Aspect
public class PermissionAspect {
    private static final String TAG = "yuyidong";

    @Pointcut("execution(@me.shouheng.notepal.aspect.permission.AspectPermission * *(..)) && @annotation(permission)")
    public void pointcut2CheckAndRequestPermissions(Permission permission) {
    }

    @Around("pointcut2CheckAndRequestPermissions(permission)")
    public void aroundCheckAndRequestPermission(ProceedingJoinPoint proceedingJoinPoint, Permission permission) {
        if (permission == null) {
            return;
        }
        IPresenter iPresenter = (IPresenter) proceedingJoinPoint.getTarget();
        int value = permission.value();
        switch (value) {
            case PermissionUtils.CODE_CAMERA:
            case PermissionUtils.CODE_ADJUST_CAMERA: {
                if (hasPermission4Camera(PermissionInstance.context)) {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                    }
                } else {
                    if (iPresenter.getIView().getRequestType().isActivity()) {
                        requestPermissions4Activity(iPresenter.getIView().getRequestType().getActivity(), PermissionInstance.context.getString(R.string.permission_camera_init),
                                PermissionUtils.PERMISSION_CAMERA, value);
                    } else {
                        requestPermissions4Fragment(iPresenter.getIView().getRequestType().getFragment(), PermissionInstance.context.getString(R.string.permission_camera_init),
                                PermissionUtils.PERMISSION_CAMERA, value);
                    }
                }
            }
            break;
            case PermissionUtils.CODE_STORAGE: {
                if (hasPermission4Storage(PermissionInstance.context)) {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                    }
                } else {
                    if (iPresenter.getIView().getRequestType().isActivity()) {
                        requestPermissions4Activity(iPresenter.getIView().getRequestType().getActivity(), PermissionInstance.context.getString(R.string.permission_storage_init),
                                PermissionUtils.PERMISSION_STORAGE, value);
                    } else {
                        requestPermissions4Fragment(iPresenter.getIView().getRequestType().getFragment(), PermissionInstance.context.getString(R.string.permission_storage_init),
                                PermissionUtils.PERMISSION_STORAGE, value);
                    }
                }
            }
            break;
            case PermissionUtils.CODE_LOCATION_AND_CAMERA: {
                if (hasPermission4LocationAndCamera(PermissionInstance.context)) {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                    }
                } else {
                    if (iPresenter.getIView().getRequestType().isActivity()) {
                        requestPermissions4Activity(iPresenter.getIView().getRequestType().getActivity(), PermissionInstance.context.getString(R.string.permission_location),
                                PermissionUtils.PERMISSION_LOCATION_AND_CAMERA, value);
                    } else {
                        requestPermissions4Fragment(iPresenter.getIView().getRequestType().getFragment(), PermissionInstance.context.getString(R.string.permission_location),
                                PermissionUtils.PERMISSION_LOCATION_AND_CAMERA, value);
                    }
                }
            }
            break;
            case PermissionUtils.CODE_AUDIO: {
                if (hasPermission4Audio(PermissionInstance.context)) {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                    }
                } else {
                    if (iPresenter.getIView().getRequestType().isActivity()) {
                        requestPermissions4Activity(iPresenter.getIView().getRequestType().getActivity(), PermissionInstance.context.getString(R.string.permission_audio),
                                PermissionUtils.PERMISSION_AUDIO, value);
                    } else {
                        requestPermissions4Fragment(iPresenter.getIView().getRequestType().getFragment(), PermissionInstance.context.getString(R.string.permission_audio),
                                PermissionUtils.PERMISSION_AUDIO, value);
                    }
                }
            }
            break;
            case PermissionUtils.CODE_PHONE_STATE: {
                if (hasPermission4PhoneState(PermissionInstance.context)) {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                    }
                } else {
                    if (iPresenter.getIView().getRequestType().isActivity()) {
                        requestPermissions4Activity(iPresenter.getIView().getRequestType().getActivity(), PermissionInstance.context.getString(R.string.permission_phone_state),
                                PermissionUtils.PERMISSION_PHONE_STATE, value);
                    } else {
                        requestPermissions4Fragment(iPresenter.getIView().getRequestType().getFragment(), PermissionInstance.context.getString(R.string.permission_phone_state),
                                PermissionUtils.PERMISSION_PHONE_STATE, value);
                    }
                }
            }
            break;
        }
    }

    @After("execution(* android.support.v4.app.FragmentActivity.onRequestPermissionsResult(..))")
    public void afterPermissionRequestBack(JoinPoint joinPoint) {
        Object[] objects = joinPoint.getArgs();
        Object object = joinPoint.getTarget();
        if (objects.length >= 1 && objects[0] instanceof Integer && object != null && object instanceof IView && ((IView) object).getPresenter() != null) {
            int requestCode = (int) objects[0];
            invokeMethod(((IView) object).getPresenter(), requestCode);
        } else {
        }
    }

    @After("execution(* android.app.Fragment.onRequestPermissionsResult(..))")
    public void afterPermissionRequestBack4Fragment(JoinPoint joinPoint) {
        Object[] objects = joinPoint.getArgs();
        Object object = joinPoint.getTarget();
        if (objects.length >= 1 && objects[0] instanceof Integer && object != null && object instanceof IView && ((IView) object).getPresenter() != null) {
            int requestCode = (int) objects[0];
            invokeMethod(((IView) object).getPresenter(), requestCode);
        } else {
        }
    }

    private static void invokeMethod(@NonNull IPresenter iPresenter, @NonNull int requestCode) {
        Class clazz = iPresenter.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Permission.class)) {
                Permission ann = method.getAnnotation(Permission.class);
                if (ann.value() == requestCode) {
                    if (method.getParameterTypes().length > 0) {
                        throw new RuntimeException("Cannot execute non-void method " + method.getName());
                    }
                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(iPresenter);
                    } catch (IllegalAccessException e) {
                    } catch (InvocationTargetException e) {
                    }
                }
            }
        }
    }

    private static void requestPermissions4Activity(final @NonNull Activity activity, String explanation, final String[] permissions, final int code) {
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                shouldShowRationale = true;
                break;
            }
        }
        if (shouldShowRationale) {
            //explanation
            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.note_dialog)
                    .setTitle(R.string.permission_title)
                    .setMessage(explanation)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_btn_ok, (dialogCallback, which) ->
                            ActivityCompat.requestPermissions(activity, permissions, code))
                    .setNegativeButton(R.string.dialog_btn_cancel, null)
                    .create();
            dialog.show();
        } else {
            ActivityCompat.requestPermissions(activity, permissions, code);
        }
    }

    private static void requestPermissions4Fragment(final @NonNull Fragment fragment, String explanation, final String[] permissions, final int code) {
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
                shouldShowRationale = true;
                break;
            }
        }
        if (shouldShowRationale) {
            //explanation
            AlertDialog dialog = new AlertDialog.Builder(fragment.getActivity(), R.style.note_dialog)
                    .setTitle(R.string.permission_title)
                    .setMessage(explanation)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_btn_ok, (dialogCallback, which) ->
                            FragmentCompat.requestPermissions(fragment, permissions, code))
                    .setNegativeButton(R.string.dialog_btn_cancel, null)
                    .create();
            dialog.show();
        } else {
            FragmentCompat.requestPermissions(fragment, permissions, code);
        }
    }


    private static boolean hasPermission4Camera(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasPermission4Storage(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission0 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission0 == PackageManager.PERMISSION_GRANTED && permission1 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasPermission4LocationAndCamera(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission0 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission2 = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (permission0 == PackageManager.PERMISSION_GRANTED && permission1 == PackageManager.PERMISSION_GRANTED &&
                permission2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasPermission4Audio(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasPermission4PhoneState(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
