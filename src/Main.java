import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.AbstractSimilarity;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.JaccardIndexSimilarity;
import net.sf.javaml.distance.MaxProductSimilarity;
import net.sf.javaml.distance.NormalizedEuclideanSimilarity;
import net.sf.javaml.distance.dtw.DTWSimilarity;
import net.sf.javaml.tools.InstanceTools;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import back.SentiWordNet;
import back.StnPrsrController;
import back.TwitterController;
import back.WS4J;

public class Main {
	
	
	
	private static TwitterController twtcntrl;
	private static StnPrsrController stnprsr;
	private static AspectExtractor ae;
	private static WS4J ws;
	private static KMedoids km;
	private static SentiWordNet sent;

	public static void main(String[] args) {
		
		try {
			sent = new SentiWordNet("Sentiwordnet/SentiWordNet_3.0.0.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			Vector<String> messages = new Vector<String>();
			
			for(int i = 0; i < 30; i++)
			{
				try {
					messages.addElement(read(i+1));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

//			messages.addElement("music was bad");
//			messages.addElement("but the music was really great");
//			messages.addElement("music had no originality");
//			
//			messages.addElement("food was stale");
//			messages.addElement("no food had any quality");
//			messages.addElement("food was bad");
//			
//			messages.addElement("act was bad");
//			messages.addElement("acting was embarrassing");
//			messages.addElement("actor was great");
//			
//			messages.addElement("the lightning was great");
//			messages.addElement("the lightning was fun");
//			messages.addElement("great lightning");
//			
//			messages.addElement("stale soundtrack");
//			messages.addElement("sound effects were uninteresting");
//			messages.addElement("no variety on sound effects");
//			
			
			Vector<String[]> aspects = new Vector<String[]>();
			
			messages = treatInputs(messages);
			
			for(String s:messages)
			{
				aspects.addAll(getAspects(s));
			}
			
			for(String[] k:aspects)
			{
				System.out.println(k[0]+" "+k[1]);
			}

			Vector<String> tokens = tokenizeAspects(aspects);
			
			double[][] matriz = computeJCNMatrix(aspects, tokens);
			
			
			
			Set<String> a = new HashSet<String>();
			
			for(String token:tokens)
			{
				a.add(token);
			}
			
			for(String p:a)
			{
				//System.out.println(p);
			}
		
			
			Dataset[] clusters = startKmd(messages.size(),1000, matriz);

//			for(Dataset stuff:clusters)
//			{
//				System.out.print("Cluster[");
//				for(Instance p:stuff)
//				{
//					System.out.print(p.getID()+",");
//				}
//				System.out.println("]");
//			}
			
			Vector<Pair> aspectValues = new Vector<Pair>();
			
			for(int i = 0; i < clusters.length; i++)
			{
				if(!clusters[i].isEmpty())
				{
					double max = 0;
					int currkey = 0;
					for(Instance p:clusters[i])					
					{
						int key = p.getID();
						double currv = 0;
						for(Instance o:clusters[i])
						{
							currv+= matriz[key][o.getID()];
						}
						if(currv>max)
						{
							max = currv;
							currkey = key;
						}
					}
					Pair tempPair = new Pair<String, Double>(aspects.elementAt(currkey)[1],(double) 0);
					
					for(Instance p:clusters[i]){
						String[] splited = aspects.elementAt(p.getID())[0].split("\\s+");
						for(String s:splited)
						{
							double p1 = sent.extract(s, "a");
							double k = (double) tempPair.second;
							tempPair.setSecond(p1+k);
						}
					}
					
					aspectValues.addElement(tempPair);
				}
			}
			
			for(Pair k:aspectValues)
			{
				System.out.print(k.first+": ");
				System.out.println(k.second);
			}
	}
	
	
	public static String read(int n) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader("featureset/"+n+".txt"));
		String line = in.readLine();
		in.close();
		return line;
	}
	
	public static Vector<String[]> getAspects(String tweetText)
	{
		Vector<String[]> results = new Vector<String[]>();
		
		Tree a = stnprsr.parseString(tweetText);
		Collection<TypedDependency> b = stnprsr.findDependencies(a);
		//System.out.println(b);
		results = ae.extractAspects(b);
		
		return results;
	}
	
	public static class Pair<A, B>
	{
		private A first;
	    private B second;

	    public Pair(A first, B second) {
	        super();
	        this.first = first;
	        this.second = second;
	    }
	    
	    public A getFirst() {
	        return first;
	    }

	    public void setFirst(A first) {
	        this.first = first;
	    }

	    public B getSecond() {
	        return second;
	    }

	    public void setSecond(B second) {
	        this.second = second;
	    }
	    public int hashCode() {
	        int hashFirst = first != null ? first.hashCode() : 0;
	        int hashSecond = second != null ? second.hashCode() : 0;

	        return (hashFirst + hashSecond) * hashSecond + hashFirst;
	    }

	    public boolean equals(Object other) {
	        if (other instanceof Pair) {
	            Pair otherPair = (Pair) other;
	            return 
	            ((  this.first == otherPair.first ||
	                ( this.first != null && otherPair.first != null &&
	                  this.first.equals(otherPair.first))) &&
	             (  this.second == otherPair.second ||
	                ( this.second != null && otherPair.second != null &&
	                  this.second.equals(otherPair.second))) );
	        }

	        return false;
	    }

	    public String toString()
	    { 
	           return "(" + first + ", " + second + ")"; 
	    }
	}
	
	public static double[][] computeJCNMatrix(Vector<String[]> lista, Vector<String> tokenized)
	{
		int n = lista.size();
		
		double[][] matrix = new double[n][n];
		
		for(int i = 0; i < n; i++)
		{
			for(int k = 0; k < n; k++)
			{
				
				String s1 = lista.elementAt(i)[1],s2 = lista.elementAt(k)[1];
				double value = ws.runJCN(s1,s2);
				matrix[i][k] = value;
			}
		}
		
		//debugMatrix(matrix);
		
		for(int i = 0; i < tokenized.size(); i++)
		{
			
			for(int o = 0; o < tokenized.size(); o++)
			{
				double stemV = ws.runJCN(tokenized.elementAt(i), tokenized.elementAt(o));
				matrix[i][o] = stemV;
		
			}
		}
		//debugMatrix(matrix);
		
		return matrix;
	}
	
	public static Vector<String> tokenizeAspects(Vector<String[]> aspects)
	{
		Vector<String> returned = new Vector<String>();
		
		for(String[] e:aspects)
		{
			returned.addElement(stnprsr.tokenizeString(e[1]));
		}
		
		return returned;
	}
	
	public static Dataset[] startKmd(int cluster_n, int max_iteractions, double[][] matrix)
	{
		km = new KMedoids(cluster_n,max_iteractions, new MaxProductSimilarity());
		
		Instance tmpInstance = null;
		Dataset data = new DefaultDataset();
		
		for(int i = 0; i < matrix.length; i++)
		{
			tmpInstance = new DenseInstance(matrix[i]);
			data.add(tmpInstance);
		}		
		
		Dataset[] clusters = km.cluster(data);
		
		return clusters;
		
	}
	
	public static void debugMatrix(double[][] matriz)
	{
		for(int i = 0; i < matriz.length; i++)
		{	
			for(int o = 0; o < matriz.length; o++){
				System.out.printf("%f	",matriz[i][o]);
			}
			System.out.println();
		}
	}
	
	public static Vector<String> treatInputs(Vector<String> k)
	{
		Vector<String> returned = new Vector<String>();
		
		for(String s:k)
		{
			String finalS = "";
			String[] splited = s.split("\\s+");
			for(String p:splited)
			{
				if(!p.contains("#") && !p.contains("@"))
				{
					finalS+= p +" ";
				}
			}
			returned.add(finalS);
		}
		
		return returned;
	}
	
}
