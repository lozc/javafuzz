/*
 * Maintainer: Emmanouel Kellinis (me@cipher.org.uk) 
 * Java Classes Fuzzer - Reflection Based
 * http://www.cipher.org.uk
 */

package javafuzz;

import gnu.getopt.Getopt;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author 
 */
public class javaFuzz {
    
    /** Creates a new instance of javaFuzz */
    public javaFuzz()  {}
    
    /**
     * @param args the command line arguments
     */ 
    
    //Static Values Init 
    public static int Exceed =0;
    //Global Overflowing Data
    public static  int    ExceedInt   =0;// Integer.MAX_VALUE;
    public static  double ExceedDouble=0;// Double.MAX_VALUE;
    public static  float  ExceedFloat =0;// Float.MAX_VALUE;
    public static  short  ExceedShort =0;// Short.MAX_VALUE;
    public static  long   ExceedLong  =0;// Short.MAX_VALUE;
    //Set Values
    static public  short smin = Short.MIN_VALUE;;
    static public  short smax = Short.MAX_VALUE;;
    static public  int   imin = Integer.MIN_VALUE;;
    static public  int   imax = Integer.MAX_VALUE;;
    static public  long  lmin = Long.MIN_VALUE;;
    static public  long  lmax = Long.MAX_VALUE;;
    static public  float fmin = Float.MIN_VALUE;;
    static public  float fmax = Float.MIN_VALUE;;
    static public  double dmin=Double.MIN_VALUE;;
    static public  double dmax=Double.MAX_VALUE;;
    public static int  etternalLoop=0;
    //Recursion 
    public static  int   Recursion  =23;// Default
    //Array Size
    public static  int   ArraySize  =800;// Default
    //String size
    public static  int   StringSize  =1024;// Default
    //String starting text
    public static String Start="";
    //Methods Attack
    public static int attackMethods =0;
    //Enumerate Constant's position
    public static int oo=0;
    //Create Helper class
    public static Helper  help = new Helper();
    //MAX or MIN Values
    public static String limit="";

    
        
public static void main(String[] args) {
String[] argv= args;
Getopt g = new Getopt("JavaFuzz", argv, ":vf:c:e:s:r:a:k:l:mou:");
int c;
String arg;
int vv=0,rr=0;
String ff="",ee="",cc="",ss="";

//Command Line Arguments
 while ((c = g.getopt()) != -1)
   {
     switch(c)
       {
          case 'v':
             //Verbose
            vv=1;
            break;
          case 'o':
             //Verbose
            oo=1;
            break;
           case 'm':
             //Methods Attack Flag
            attackMethods=1;
            break;
          case 'f':
            //Classes File
            arg = g.getOptarg();
            ff=arg;
            break;
          case 'k':
            //Set Value 
            arg = g.getOptarg();
            String[] values = arg.split("=");
            if (values.length!=2 ){usage();System.exit(0);}
            else {
            if (values[0].equals("int")){ 
            imax =  Integer.parseInt(values[1]);
            imin = -Integer.parseInt(values[1]);
            }
            else if (values[0].equals("float")){ 
            fmax =  Float.parseFloat(values[1]);
            fmin = -Float.parseFloat(values[1]);
            }
            else if (values[0].equals("double")){ 
            dmax =  Double.parseDouble(values[1]);
            dmin = -Double.parseDouble(values[1]);
            }
            else if (values[0].equals("short")){ 
            smax =  Short.parseShort(values[1]);
            }
            else if (values[0].equals("long")){ 
            lmax =  Long.parseLong(values[1]);
            lmin = -Long.parseLong(values[1]);
            }
            else {usage();System.exit(0);}
            }
            
            break;

          case 'c':
            //Class
            arg = g.getOptarg();
            cc=arg;
            break;
          case 'e':
            //Extend
            arg = g.getOptarg();
            ee=arg;
            break;
          case 'r':
            //Recursions
            arg = g.getOptarg();
            try {Recursion= Integer.parseInt(arg);} catch (Exception e){usage();System.exit(0);}
            break;
          case 'a':
            //Array Size
            arg = g.getOptarg();
            try {ArraySize= Integer.parseInt(arg);} catch (Exception e){usage();System.exit(0);}
            break;
          case 'l':
            //String Size
            arg = g.getOptarg();
            try {StringSize= Integer.parseInt(arg);} catch (Exception e){usage();System.exit(0);}
            break;
          case 'u':
            //String Size
            arg = g.getOptarg();
            try {limit= arg;} catch (Exception e){usage();System.exit(0);}
            break;
          case 's':
           //String
            arg = g.getOptarg();
            ss=arg;
            Start=ss;
            break;
          case ':':
            usage();
            break;
          case '?':
            //usage();  
            break; // getopt() already printed an error
            //
         default:
            usage();
            break;
       }
   }
         if     (ee.equals("int"))      { ExceedInt    = Integer.MAX_VALUE;}
         else if(ee.equals("double"))   { ExceedDouble = Double.MAX_VALUE; }
         else if(ee.equals("float"))    { ExceedFloat  = Float.MAX_VALUE;  }
         else if(ee.equals("short"))    { ExceedShort  = Short.MAX_VALUE;  }
         else if(ee.equals("long"))     { ExceedLong   = Long.MAX_VALUE;   }

       if ((!ff.equals("") && !cc.equals(""))||(ff.equals("") && cc.equals(""))){usage();}
       else {
    
       if      (!cc.equals("")) {
                try {   summarize(cc,vv);
                } catch (Exception ex) {
                  usage();
                  System.out.println("+Invalid Class");
                }}
       else if (!ff.equals("")) {
                try {   recursiveAttack(ff,vv);
                } catch (Exception ex) {
                  usage();
                  System.out.println("+Classes File ERROR");
                }}
       }
        
       
        
    }
   
  public static Object Constant =null;
  public static int enumerateConstant =1;
  public static void summarize(String className, int v)

  { try{
    Exceed=1;
    Class cls = Class.forName(className);
    Constant = help.returnConsant(cls);
    if (help.returnConsant(cls)!=null) 
    {System.out.println("\nNOTE: This class takes Constant values. Try -o flag\n");}
 
    Constructor[] a = cls.getConstructors();
    Object[] args ;
    System.out.println("--------------------------------------");  
    for (int f=0;f<a.length;f++){
       Class[] ff =  a[f].getParameterTypes();
       Class[] types =  ff ;
       System.out.print("Constructor -> \t"+a[f].getName()+"\nTypes -> \t(");
       for (int k=0;k<ff.length;k++)
       {System.out.print(" "+ff[k].getName());}
       System.out.print(" )\n");
       System.out.println("Invoke -> \t"+className);
       Constructor cons = cls.getConstructor(types);
       
       //High Values - No Methods
       etternalLoop=0;
       if (limit.equals("high") | limit.equals("") ){
       args =  slapObject(ff,1,Exceed) ;
       System.out.print("\n[MAX] Status -> \t");
	   help.BeefConstructor(cons,args,help.returnConsant(cls),v,oo);
       System.out.print("\n");
       }   
       //Low Values - No Methods
       if (limit.equals("low") | limit.equals("") ){
       args =  slapObject(ff,0,Exceed) ;
       System.out.print("[MIN] Status -> \t");
	   help.BeefConstructor(cons,args,help.returnConsant(cls),v,oo);
       System.out.print("\n");	
       }
       //Method Attack
       if (attackMethods==1){
       //Hi Values
       if (limit.equals("high") | limit.equals("") ){    
       args =  slapObject(ff,1,Exceed) ;
	   methodSlap(a[f],cls,args,1,v);
       }
       //Low Values
       if (limit.equals("low") | limit.equals("") ){
       args =  slapObject(ff,0,Exceed) ;
       methodSlap(a[f],cls,args,0,v);
       }
       }
     
       System.out.println("--------------------------------------");
       
      
   }
    } catch(Exception e){}
  }
  
  public static void DoIt (Constructor cons, Object[] args, int v){
       try {
	   cons.newInstance(args);
       System.out.print(" No Problem\n");
       }
       catch(Exception e){
       if (v==1){ System.out.print("Exception("+e.getCause() +")\n");}
       else {System.out.print("Exception\n");}
       }
   }
    public static void DoIt (Constructor cons, int v){
       try {
	   cons.newInstance();
       System.out.print(" No Problem\n");
       }
       catch(Exception e){
       if (v==1){ System.out.print("Exception("+e.getCause() +")\n");}
       else {System.out.print("Exception\n");}
       }
   }
   
        
  
  
  //Expand Methods and throw Slaped Objects in
  public static void  methodSlap(Constructor cs,Class cls,Object[] args,int hilo,int v)  {
  try {
  Method[] allMethods = cls.getDeclaredMethods();
  String hilow;
  System.out.println("\n~~ Methods Fuzzing ~~");
  Object tmpCLS = help.returnConsant(cls);
  
  for (int a=0;a<allMethods.length;a++)
  {             Class[] cc = allMethods[a].getParameterTypes();
                etternalLoop=0;
                Object[] MethodArgs =  slapObject(cc,hilo,Exceed) ;
                System.out.print("\nMethod -> \t"+allMethods[a].getName()+" ["+cls.getName()+"]\nTypes -> \t(");
                for (int k=0;k<cc.length;k++){System.out.print(" "+cc[k].getName());}
                System.out.print(" )\n");
                help.BeefConstructor(cs,args,allMethods[a],tmpCLS,MethodArgs,v,oo);
  }
  System.out.println("\n~~~~~~~~~~~~~~~~~~~~~");
  }
  catch(Exception e){}
   
  }
  
  

public static Object[] slapObject (Class[] cls,int hilow,int E) {
    etternalLoop++;;        
    Object[] list = new Object[cls.length];
     try{
    E=0;
    byte bmin = -128;
    byte bmax = 127;
    byte[] ab= new byte[ArraySize];//{bmax,bmin};
    //Multi-dimensional arrays -- monkey business at the moment will do it more genericly soon
    byte[][] abb= new byte[ArraySize][ArraySize];//{bmax,bmin};
    //Limits : short -32,768 and a maximum value of 32,767
    short[] as= new short[ArraySize];//{smax,smin};
    short[][] ass= new short[ArraySize][ArraySize];//{smax,smin};
    //Limits : int minimum value of -2,147,483,648 and a maximum value of 2,147,483,647 
    int[] ai= new int[ArraySize];//{imax,imin};
    int[][] aii= new int[ArraySize][ArraySize];//{imax,imin};
    //Limits : long minimum value of -9,223,372,036,854,775,808 and a maximum value of 9,223,372,036,854,775,807
    long lmax= Long.MAX_VALUE;;
    long[] al= new long[ArraySize];//{lmax,lmin};
    long[][] all= new long[ArraySize][ArraySize];
    //Limits : float  single-precision 32-bit IEEE 754 floating point
    float[] af= new float[ArraySize];//{fmax,fmin};
    float[][] aff= new float[ArraySize][ArraySize];//{fmax,fmin};
    //Limits : double double-precision 64-bit IEEE 754 floating point
    double[] ad= new double[ArraySize];//{dmax,dmin};
    double[][] add= new double[ArraySize][ArraySize];//{dmax,dmin};
    //Limits : boolean true/false - this one doesnt make much sense but anyways
    boolean bomin=false;
    boolean bomax=true;
    boolean[] abo= new boolean[ArraySize];//{bomax,bomin};
    boolean[][] aboo= new boolean[ArraySize][ArraySize];//{bomax,bomin};
    //Limits : char '\u0000' to '\uffff' 
    char cmin='\u0000';
    char cmax='\uffff';
    char[] ac= new char[ArraySize];//{cmax,cmin};
    char[][] acc= new char[ArraySize][ArraySize];//{cmax,cmin};
    //Limits : string 
    String stmin ="1";
    String stmax="";
    if (Start.equals("")){
    stmax = BigString("1",StringSize);
    stmax = Start+stmax;}
    else{stmax=Start;stmin=Start;}
    
    String[] ast = new String[ArraySize];//{stmin,stmax};
    String[][] astt = new String[ArraySize][ArraySize];//{stmin,stmax};
    
    for (int k=0;k<cls.length;k++){
    String current = cls[k].getName();     
    boolean max=false;
    if(hilow==1){max=true;}
    if (current.equals("int")) {
        if(max){list[k]=(imax+ExceedInt);}
        else {list[k]=imin-(ExceedInt);}
    }
    else if (current.equals("[I")){list[k]=ai;}
    else if (current.equals("[[I")){list[k]=aii;}
    else if (current.equals("char")){
        if(max){list[k]=cmax;}
        else {list[k]=cmin;}
   }
    else if (current.equals("[C")){list[k]=ac;}
    else if (current.equals("[[C")){list[k]=acc;}
    else if (current.equals("float")){
        if(max){list[k]=fmax+ExceedFloat;}
        else {list[k]=fmin-(ExceedFloat);}
   }
    else if (current.equals("[F")){list[k]=af;}
    else if (current.equals("[[F")){list[k]=aff;}
    else if (current.equals("short")){
        if(max){list[k]=smax+ExceedShort;}
        else {list[k]=smin-(ExceedShort);}
   }
    else if (current.equals("[S")){list[k]=as;}
    else if (current.equals("[[S")){list[k]=ass;}
    else if (current.equals("boolean")){
        if(max){list[k]=bomax;}
        else {list[k]=bomin;}
   }
    else if (current.equals("[Z")){list[k]=abo;}
    else if (current.equals("[[Z")){list[k]=aboo;}
    else if (current.equals("double")){
        if(max){list[k]=dmax+ExceedDouble;}
        else {list[k]=dmin-(ExceedDouble);}
   }
    else if (current.equals("[D")){list[k]=ad;}
    else if (current.equals("[[D")){list[k]=add;}
    else if (current.equals("long")){
        if(max){list[k]=lmax+ExceedLong;}
        else {list[k]=lmin-(ExceedLong);}
   }
    else if (current.equals("[J")){list[k]=al;}
    else if (current.equals("[[J")){list[k]=all;}
    else if (current.equals("byte")){
        if(max){list[k]=bmax;}
        else {list[k]=bmin;}
   }
    else if (current.equals("[B")){list[k]=ab;}
    else if (current.equals("[[B")){list[k]=abb;}
    else if (current.equals("java.lang.String")){
        if(max)
        {
            list[k]=stmax;
        }
        else {list[k]=stmin;}
   }
    else if (current.equals("[Ljava/lang/String")){list[k]=ast;}
    else if (current.equals("[[Ljava/lang/String")){list[k]=astt;}
    //Construct - Uknown Object
    else {
    
	    try { 
		    Class clsa = Class.forName(current);
        	Object Constant1 = help.returnConsant(clsa);
	        Constructor[] a = clsa.getConstructors();
        	Object[] args ;
            int check=0;
        	for (int f=0;f<a.length;f++)   {
	        Class[] ff =  a[f].getParameterTypes();
            Constructor cons = clsa.getConstructor(ff);
             if   (etternalLoop<Recursion){args =  slapObject(ff,1,0) ;}
             else {System.out.println("\n****Infinite Loop detected (use -r)****"); args=null;}

             		if (args.length>0){
                          for(int h=0;h<args.length;h++){ 
                          Object[] tmpr = new Object[args.length];
                          for (int p=0;p<args.length;p++) {tmpr[p]=args[p];}
                                //Show Submitted Values
                                System.out.print("\nSubmit Values - Sub-constructor ("+clsa.getName()+"):\n\t ");
                                for (int display=0;display<args.length;display++)
                                {System.out.print(tmpr[display]+" ");}                    
                                try   {list[k]=cons.newInstance(tmpr);System.out.print(":No Problem\n");check=1;break;}
                                catch (Exception e){System.out.print(""+e.getCause());
                                try   {list[k]=cons.newInstance(args);System.out.print(":No Problem\n");check=1;break;}
                                catch (Exception ea){} 
                                }                    						   
                              }
						}
			
					else { try    {list[k]=cons.newInstance();check=1;break;} catch  (Exception e){list[k]=null;break;} }       	
        	    
				if (check==1) {break;}
   				}
   				
   				if (check!=1) {list[k]=null;}
		    
		     }
    	catch (Exception e){System.out.println("hat?"+e);}
    	
    	
    	}//Major ELSE
    
    }
    
    return list;}
    
    
    
    
    
    catch(Exception e){/*Not able to construct types*/ }
    return list;
    }

    
        
static String BigString (String str,int size){
String tmp="";
for (int a=0;a<size;a++){tmp=tmp+str;}
return tmp; }
   
   
   

    
static void recursiveAttack(String FileName,int v) throws Exception {
    
     
                               
       FileInputStream fstream = new FileInputStream(FileName);
	 DataInputStream in = new DataInputStream(fstream);
             while (in.available() !=0)
		 { try {  javaFuzz.summarize(in.readLine(),v);}catch(Exception e){} }
            in.close();
         
}

    private static void usage() {
                System.out.println("\nJavaFuzzer - Classes Fuzzing (Reflection Based)\n");
               String output =
                                "\n"+"FLAGS"+
                                "\n"+"-v: Verbose - Fully Print Exceptions"+
                                "\n"+"-m: Fuzz methods of a Class, Can take Long time to finish"+
                                "\n"+"-f: Read Class names from a file"+
                                "\n"+"-c: Input is Class name, you cannot use -f at the same time"+
                                "\n"+"-s: You can set the fuzzing String, for example http://www.example.com"+
                                "\n"+"-e: You can set the type you want to overflow with the MAX_VALUE on top "+
                                "\n"+"    Values can be : int or double or float or long or short"+
                                "\n"+"-r: Number of recursions until constructs the class [Default 20]"+
                                "\n"+"    If needs more it will set type to null and consider it Infinite"+
                                "\n"+"-k: Set the value for int,float,long,short,double"+
                                "\n"+"    e.g. -k int=100  or -k double=20000 and so on"+                               
                                "\n"+"-a: Set size of used array when fuzzing  [Default 800]" +
                                "\n"+"-l: Set size of used String when fuzzing [Default 1024]"+
                                "\n"+"-o: Enumerate possible constructor's Constant parameters"+
                                "\n"+"    Bruteforce's all possible positions for the constant (extra delay)"+
                                "\n"+"-u: Fuzz only high or low values respectively e.g. Integer high is +MAX_VALUE"+
                                "\n"+"    and low value is -MAX_VALUE (or MIN_VALUE). -u low or -u high "+
                                "\n\n"+"EXAMPLES"+
                                ""+""+
                                "\n"+"java -jar JavaFuzz.jar -c java.lang.String -v"+
                                "\n"+"java -jar JavaFuzz.jar -f classes.txt -v -e int"+
                                "\n"+"java -jar JavaFuzz.jar -c java.net.URL -e int -s http://www.example.com";
               System.out.println(output);

    }
    
    
   
 
}