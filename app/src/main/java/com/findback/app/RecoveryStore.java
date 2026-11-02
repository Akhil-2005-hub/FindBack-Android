package com.findback.app;
import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Local-only code storage, failed-attempt lockout and response cooldown. */
final class RecoveryStore {
    private static final String PREF="findback_private";
    private static final Pattern COMMAND=Pattern.compile("(?i)(?:^|\\b)FIND\\s+([0-9]{8})(?:\\b|$)");
    private static final long WINDOW=15*60_000L, LOCK=15*60_000L, COOLDOWN=45_000L;
    enum Check { OK, WRONG, LOCKED, NOT_CONFIGURED }
    private RecoveryStore() { }
    static boolean validCode(String code){return code!=null&&code.matches("[0-9]{8}");}
    private static SharedPreferences p(Context c){ return c.getSharedPreferences(PREF,Context.MODE_PRIVATE); }
    static synchronized void saveCode(Context c,String code){ byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt); p(c).edit().putString("salt",hex(salt)).putString("hash",hash(salt,code)).putInt("bad",0).putLong("window",0).putLong("locked",0).putLong("reply",0).apply(); }
    static boolean configured(Context c){return !p(c).getString("hash","").isEmpty();}
    static boolean armed(Context c){return p(c).getBoolean("armed",false);}
    static void setArmed(Context c,boolean v){p(c).edit().putBoolean("armed",v).apply();}
    static boolean beep(Context c){return p(c).getBoolean("beep",false);}
    static void setBeep(Context c,boolean v){p(c).edit().putBoolean("beep",v).apply();}
    static String extract(String body){if(body==null)return null; Matcher m=COMMAND.matcher(body);return m.find()?m.group(1):null;}
    static synchronized Check check(Context c,String code){SharedPreferences q=p(c);String sh=q.getString("salt","");String expected=q.getString("hash","");if(sh.isEmpty()||expected.isEmpty())return Check.NOT_CONFIGURED;long now=System.currentTimeMillis();if(now<q.getLong("locked",0))return Check.LOCKED;byte[] salt=unhex(sh);if(MessageDigest.isEqual(unhex(hash(salt,code)),unhex(expected))){q.edit().putInt("bad",0).putLong("window",0).apply();return Check.OK;}long start=q.getLong("window",0);int count=q.getInt("bad",0);if(start==0||now-start>WINDOW){start=now;count=0;}count++;SharedPreferences.Editor e=q.edit();if(count>=5)e.putLong("locked",now+LOCK).putInt("bad",0).putLong("window",0);else e.putLong("window",start).putInt("bad",count);e.apply();return Check.WRONG;}
    static synchronized boolean allowReply(Context c){long now=System.currentTimeMillis(),last=p(c).getLong("reply",0);if(now-last<COOLDOWN)return false;p(c).edit().putLong("reply",now).apply();return true;}
    private static String hash(byte[] salt,String code){PBEKeySpec spec=new PBEKeySpec(code.toCharArray(),salt,120_000,256);try{return hex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());}catch(Exception e){throw new IllegalStateException(e);}finally{spec.clearPassword();}}
    private static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format(Locale.US,"%02x",x&255));return s.toString();}
    private static byte[] unhex(String s){byte[] b=new byte[s.length()/2];for(int i=0;i<b.length;i++)b[i]=(byte)Integer.parseInt(s.substring(i*2,i*2+2),16);return b;}
}
