package edu.ucsd.ncmir.gridwrap.util;


/** Class for accessing files to a secure server via
SSL.  This allows you to acess files using the https protocol.
Currently java has no support for https thus this class is necessary
Using this class avoids having to dowload a certicate for the client
to trust the server.

A simple example of connecting to a secure server:

HttpsSSLConnection hts = new HttpsSSLConnection();
HttpsURLConnection urlcon =(HttpsURLConnection)hts.connecttowebsite(u);
urlcon.setUseCaches(false);
urlcon.setDoInput(true);
urlcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
urlcon.setDoOutput(false);

@author Tomas Molina
*/

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.*;
import java.net.*;
import java.awt.*;

public class HttpsSSLConnection extends Component
{

   private SSLSocketFactory factory;

   public HttpsSSLConnection() throws Exception
   {
       //System.out.println("Before MyTrustManager");
       MyTrustManager trust = new MyTrustManager();
       MyTrustManager trustArray[] = new MyTrustManager[1];
       trustArray[0] = trust;

        //System.out.println("Before KeyManagerFactory" );
       KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        //System.out.println("Before KeyStore instance" );
       KeyStore ks = KeyStore.getInstance("JKS");

        //System.out.println("Before SSLContext instance" );
       SSLContext ctx = SSLContext.getInstance("SSL");
        //System.out.println("Before SSLContext init" );
       //ctx.init(kmf.getKeyManagers(),trustArray,null);
       ctx.init(null,trustArray,null);

       //System.out.println("Before factory");
       factory = ctx.getSocketFactory();

       //System.out.println("After factory");



   }
 public HttpsURLConnection connecttowebsite(URL u) throws Exception
   {
      HttpsURLConnection urlcon = (HttpsURLConnection)u.openConnection();
      urlcon.setSSLSocketFactory(factory);
      urlcon.setHostnameVerifier(new MyHostnameVerifier());
      urlcon.setUseCaches(false);
      urlcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

      return urlcon;

   }

   public InputStream getInputStream(String s) throws Exception
   {
       URL temp = new URL(s);
       HttpsURLConnection urlcon = (HttpsURLConnection)temp.openConnection();
       urlcon.setSSLSocketFactory(factory);
       urlcon.setHostnameVerifier(new MyHostnameVerifier());
       urlcon.connect();

       return (urlcon.getInputStream());

   }

   public InputStream getInputStream(URL u) throws Exception
   {
       URL temp = u;
       HttpsURLConnection urlcon = (HttpsURLConnection)temp.openConnection();
       urlcon.setSSLSocketFactory(factory);
       urlcon.setHostnameVerifier(new MyHostnameVerifier());
       urlcon.connect();

       return (urlcon.getInputStream());

   }

   public OutputStream getOutputStream(String s)throws Exception
   {
       URL temp = new URL(s);
       HttpsURLConnection urlcon = (HttpsURLConnection)temp.openConnection();
       urlcon.setSSLSocketFactory(factory);
       urlcon.setHostnameVerifier(new MyHostnameVerifier());
       urlcon.setDoOutput(true);
       urlcon.setDoInput(false);
       urlcon.connect();

       return (urlcon.getOutputStream());
   }

   public OutputStream getOutputStream(URL u) throws Exception
   {
       URL temp = u;
       HttpsURLConnection urlcon = (HttpsURLConnection)temp.openConnection();
       urlcon.setSSLSocketFactory(factory);
       urlcon.setHostnameVerifier(new MyHostnameVerifier());
       urlcon.setDoOutput(true);
       urlcon.setDoInput(false);
       urlcon.connect();

       return (urlcon.getOutputStream());
   }
 public Image getImage(InputStream is) throws IOException
   {
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       int c;

       while ( ( c = is.read() ) >= 0 )
       {
            baos.write( c );
       }
       Image i = getToolkit().createImage(baos.toByteArray());

       return i;


   }

}

class MyTrustManager implements X509TrustManager {

  public boolean isClientTrusted( X509Certificate[] cert)
  {
    return true;
  }

  public boolean isServerTrusted( X509Certificate[] cert) {
    return true;
  }

  public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

  public void checkClientTrusted(X509Certificate[] chain,String authType)throws CertificateException
  {}
  public void checkServerTrusted(X509Certificate[] chain,String authType)throws CertificateException
  {}

}

class MyHostnameVerifier implements HostnameVerifier {

   public boolean verify(String hostname,SSLSession session)
   {
      return true;
   }

}
