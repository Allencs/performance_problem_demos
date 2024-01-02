/*
 * Decompiled with CFR.
 *
 * Could not load the following classes:
 *  com.allen.entities.CommonTestEntity
 *
 * GeneratedMethodAccessor1反编译
 */
package sun.reflect;

import com.allen.entities.CommonTestEntity;

import java.lang.reflect.InvocationTargetException;

import sun.reflect.MethodAccessorImpl;

public class GeneratedMethodAccessor1
        extends MethodAccessorImpl {
    /*
     * Loose catch block
     */
    public Object invoke(Object object, Object[] objectArray) throws InvocationTargetException {
        CommonTestEntity commonTestEntity;
        block5:
        {
            if (object == null) {
                throw new NullPointerException();
            }
            commonTestEntity = (CommonTestEntity) object;
            if (objectArray == null || objectArray.length == 0) break block5;
            throw new IllegalArgumentException();
        }
        try {
            commonTestEntity.defaultMethod();
            return null;
        } catch (Throwable throwable) {
            throw new InvocationTargetException(throwable);
        } catch (ClassCastException | NullPointerException runtimeException) {
            throw new IllegalArgumentException(super.toString());
        }
    }
}