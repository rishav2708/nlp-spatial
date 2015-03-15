/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package luceneindexdemo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.neo4j.jdbc.*;
import org.restlet.engine.util.StringUtils;


/**
 *
 * @author rishav12
 */
public class LuceneIndexDemo {

    /**
     */
    
    public static final String FILES_TO_INDEX="/home/rishav12/NetBeansProjects/LuceneIndexDemo/filestoIndex";
    public static final String INDEX_DIRECTORY="/home/rishav12/NetBeansProjects/LuceneIndexDemo/indexDirectory";
    public static final String FIELD_PATH="path";
    public static final String FIELD_CONTENTS="contents";
    public static  String user="Virat";
    public static void main(String[] args) {
        try {
            // TODO code application logic here
    //int a=10;            
//System.out.println("Enter your user name: ");
            //createIndex();
            searchIndex("i am feeling bored");
            //System.out.println("rishav"+10);
        } catch (Exception ex) {
            Logger.getLogger(LuceneIndexDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
    }
     
    public static void createIndex() throws Exception {
        Analyzer analyzer=new StandardAnalyzer();
        boolean recreateIndexIfExists=true;
        IndexWriter indexWriter=new IndexWriter(INDEX_DIRECTORY, analyzer, recreateIndexIfExists);
        File dir=new File(FILES_TO_INDEX);
        File[] files=dir.listFiles();
        for(File file:files)
        {
            Document document=new Document();
            String path=file.getCanonicalPath();
            document.add(new Field(FIELD_PATH,path,Field.Store.YES,Field.Index.UN_TOKENIZED,Field.TermVector.WITH_OFFSETS));
            Reader reader=new FileReader(file);
            document.add(new Field(FIELD_CONTENTS,reader));
            //System.out.println(document.getField(FIELD_PATH));
            //System.out.println(document.hashCode());
            indexWriter.addDocument(document);
        }
        
        indexWriter.optimize();
        indexWriter.close();
                
    }
    public static void missOperation(String type) throws FileNotFoundException, IOException, org.json.simple.parser.ParseException, SQLException
    {
        //JSONObject jsObject=new JSONObject();
        System.out.println("this brings out all your connection with the person you miss");
        String query="match (n:People)-[r:KNOWS]-(b:People) where n.name='"+user+"' and r.relType='"+type+"' return filter(x in n.interest where x in b.interest) as common,b.name";
       Connection con=DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
       ResultSet rs=con.createStatement().executeQuery(query);
       JSONParser jsParser=new JSONParser();
       FileReader freReader=new FileReader("/home/rishav12/NetBeansProjects/LuceneIndexDemo/location.json");
       JSONObject jsono=(JSONObject) jsParser.parse(freReader);
       JSONArray jslocArray=(JSONArray) jsono.get("CHENNAI");
       int count=0;
       while(rs.next())
       {
           System.out.println(rs.getString("b.name"));
           String searchQuery="start n=node:restaurant('withinDistance:["+jslocArray.get(0)+","+jslocArray.get(1)+",13.5]') where ";
           JSONArray jsArray=(JSONArray) jsParser.parse(rs.getString("common"));
           Iterator<JSONArray> iterJsArray=jsArray.iterator();
           
           count++;
           int k=0;
           int flag=0;
           while(iterJsArray.hasNext())
           {
               flag=1;
               if(k==0)
               {
                   searchQuery=searchQuery+"n.type='"+iterJsArray.next()+"'";
                   k=k+1;
                   
               }
               else
                   searchQuery=searchQuery+" or n.type='"+iterJsArray.next()+"'";
           }
           
           if(flag==1)
           {
               searchQuery+=" return n.name,n.type";
               ResultSet commonInterest=con.createStatement().executeQuery(searchQuery);
               System.out.println("Sir based on your common interests with "+rs.getString("b.name")+" \ni will plan out something nearby you");
               while(commonInterest.next())
               {
                   System.out.println(commonInterest.getString("n.name")+" "+commonInterest.getString("n.type"));
               }
           }
           else
           {
               System.err.println("you do not seem to share any common interest with"+rs.getString("b.name")); 
           }
          
       }
       return;
        
    }  
    

    public static void searchIndex(String s) throws IOException, ParseException, SQLException, FileNotFoundException, org.json.simple.parser.ParseException {
     
        Directory directory= FSDirectory.getDirectory(INDEX_DIRECTORY);
        IndexReader reader=IndexReader.open(directory);
        IndexSearcher search=new IndexSearcher(reader);
        Analyzer analyzer=new StandardAnalyzer();
        
        QueryParser queryparser=new QueryParser(FIELD_CONTENTS, analyzer);
        Query query=queryparser.parse(s);
        Hits hits=search.search(query);
        Iterator<Hit> it=hits.iterator();
        //System.out.println("hits:"+hits.length());
        float f_score;
        List<String> names=new ArrayList<>();
        while(it.hasNext())
        {
            Hit hit=it.next();
            f_score=hit.getScore();
            
            //System.out.println(f_score);
            Document document=hit.getDocument();
            Field f=document.getField(FIELD_PATH);
            
            //System.out.println(f.readerValue());
            //System.out.println(document.getValues(FIELD_PATH));
            String path=document.get(FIELD_PATH);
            System.out.println(document.getValues(path));
            Field con=document.getField(FIELD_PATH);
            //System.out.println("hit:"+path+" "+hit.getId()+" "+con);
            names.add(new File(path).getName());
        }
        
        //ProcessBuilder pb=new ProcessBuilder();
        FileReader fReader=new FileReader("/home/rishav12/NetBeansProjects/LuceneIndexDemo/inntell.json");
        JSONParser jsParser=new JSONParser();
        //System.out.println("This is an assumption that you belong to US");
        FileReader freReader=new FileReader("/home/rishav12/NetBeansProjects/LuceneIndexDemo/location.json");
        Connection con=DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
        System.out.println(names);
        if(names.get(0).equals("miss"))
        {
            
            System.out.println(s);
            StringTokenizer stringTokenizer=new StringTokenizer(s);
            Connection con1=DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
            String querySearch="match ()-[r:KNOWS]-() where has(r.relType) return distinct r.relType";
            ResultSet rSet=con.createStatement().executeQuery(querySearch);
            List<String> allRels=new ArrayList<>();
            while(rSet.next())
            {
                System.out.println();
                allRels.add(rSet.getString("r.relType"));
            }
            //System.out.println(rSet);
            
            while(stringTokenizer.hasMoreTokens())
            {
                String next=stringTokenizer.nextToken();
                 if(allRels.contains(next))
                 {
                     missOperation(next);
                 }
                //System.out.println(resSet.getString("r.relType"));
                    
            }
            System.out.println(names.get(1));
            //missOperation(names.get(1));
        }
        else
        {
        try {
            JSONObject jsonObj=(JSONObject) jsParser.parse(fReader);
            System.out.println(names.get(0));
           
            JSONObject jsObj=(JSONObject) jsonObj.get(names.get(0));
            JSONObject results=new JSONObject();
            System.out.println(jsObj.get("explaination"));
            results.put("explaination", jsObj.get("explaination"));
            JSONArray reqmts=(JSONArray) jsObj.get("true");
            System.out.println("Let me look out for the places that contains ");
            List<String> lis=new ArrayList<>();
            JSONObject locObj=(JSONObject) jsParser.parse(freReader);
            JSONArray jsArray=(JSONArray) locObj.get("CHENNAI");
            Iterator<JSONArray> ite=reqmts.iterator();
            int k=0;
            String resQuery="START n=node:restaurant('withinDistance:["+jsArray.get(0)+","+jsArray.get(1)+",7.5]') where";
           Iterator<JSONArray> ite1=reqmts.iterator();
            while(ite1.hasNext())
                System.out.println(ite1.next());
           
            while (ite.hasNext()) {
                lis.add("n.type="+"'"+ite.next()+"'");
                if(k==0)
                resQuery+=" "+lis.get(k);
                else
                    resQuery+=" or "+lis.get(k);
                //System.out.println(attrib);
                k++;
                
            }
            resQuery+=" return n.name,n.place,n.type";
            File writeTo=new File("/home/rishav12/NetBeansProjects/LuceneIndexDemo/filestoIndex1/"+names.get(0));
            FileOutputStream fop=new FileOutputStream(writeTo, true);
            String writeTOFile=s+"\n";
            fop.write(writeTOFile.getBytes());
            //System.out.println(resQuery);
            ResultSet res=con.createStatement().executeQuery(resQuery);
            JSONArray resSet=new JSONArray();
            
            while(res.next())
            {
                System.out.println(" name:"+res.getString("n.name")+" located:"+res.getString("n.place")+" type:"+res.getString("n.type"));
                JSONObject jsPart=new JSONObject();
                jsPart.put("name",res.getString("n.name"));
                jsPart.put("located",res.getString("n.place"));
                jsPart.put("type",res.getString("n.type"));
                resSet.add(jsPart);
            }
            results.put("results", resSet);
            File resultFile=new File("result.json");
           FileOutputStream fop1=new FileOutputStream(resultFile);
            System.out.println(results);
            fop1.write(results.toJSONString().getBytes());
            //String resQuery="START n=node:restaurant('withinDistance:[40.7305991, -73.9865812,10.0]') where n.coffee=true return n.name,n.address;";
            //System.out.println("Sir these results are for some coffee shops nearby NEW YORK");
            //ResultSet res=con.createStatement().executeQuery(resQuery);
            //while(res.next())
            //System.out.println("name: "+res.getString("n.name")+" address: "+res.getString("n.address"));
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(LuceneIndexDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    }
}

