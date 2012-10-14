/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\gh_workspace\\3G-Notify\\src\\jamzor\\threegnotify\\ICheckerService.aidl
 */
package jamzor.threegnotify;
public interface ICheckerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jamzor.threegnotify.ICheckerService
{
private static final java.lang.String DESCRIPTOR = "jamzor.threegnotify.ICheckerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an jamzor.threegnotify.ICheckerService interface,
 * generating a proxy if needed.
 */
public static jamzor.threegnotify.ICheckerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jamzor.threegnotify.ICheckerService))) {
return ((jamzor.threegnotify.ICheckerService)iin);
}
return new jamzor.threegnotify.ICheckerService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_enable:
{
data.enforceInterface(DESCRIPTOR);
this.enable();
reply.writeNoException();
return true;
}
case TRANSACTION_disable:
{
data.enforceInterface(DESCRIPTOR);
this.disable();
reply.writeNoException();
return true;
}
case TRANSACTION_vibrate:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.vibrate(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jamzor.threegnotify.ICheckerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void enable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void disable() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void vibrate(java.lang.String pref) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pref);
mRemote.transact(Stub.TRANSACTION_vibrate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_enable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_disable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_vibrate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void enable() throws android.os.RemoteException;
public void disable() throws android.os.RemoteException;
public void vibrate(java.lang.String pref) throws android.os.RemoteException;
}
