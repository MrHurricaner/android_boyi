package com.boyiqove.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
 
/**
 * 获取数据编码
 *
 */
public class EncodeDetector {
    /**
     * 获取流编码
     * @param in 输入流
     * @return 编码字符串
     * @throws Exception 
     */
    public static String getEncoding(BufferedInputStream buffIn) throws Exception{
//        int size = buffIn.available();
//        buffIn.mark(size);
//        CodepageDetectorProxy detector = getDetector();
//         
//        java.nio.charset.Charset charset = null;
//        charset = detector.detectCodepage(buffIn, size);
//         
//        buffIn.reset();
//         
//        return charset.displayName();
        return "";
    }
     
    /**
     * 获取二进制数组编码
     * @param byteArr 数据数组
     * @return 编码字符串
     * @throws Exception
     */
    public static String getEncoding(byte[] byteArr) throws Exception{
//        ByteArrayInputStream byteArrIn = new ByteArrayInputStream(byteArr);
//        BufferedInputStream buffIn = new BufferedInputStream(byteArrIn);
//         
//        CodepageDetectorProxy detector = getDetector();
//        Charset charset = null;
//        charset = detector.detectCodepage(buffIn, buffIn.available());
//         
//        try {
//            buffIn.close();
//        } catch (Exception e) {
//        }
//         
//        return charset.displayName();
        return "";
    }
     
//    private static CodepageDetectorProxy getDetector(){
//        if(!init){
//            detector.add(JChardetFacade.getInstance());
//            detector.add(ASCIIDetector.getInstance());
//            detector.add(UnicodeDetector.getInstance());
//            detector.add(parsingDetector);
//            detector.add(byteOrderMarkDetector); 
//             
//            init = true;
//        }
//         
//        return detector;
//        return null;
//    }
     
    private static boolean init = false;
//    private static CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
//    private static ParsingDetector parsingDetector = new ParsingDetector(false);
//    private static ByteOrderMarkDetector byteOrderMarkDetector = new ByteOrderMarkDetector();
     
}