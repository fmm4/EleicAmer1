import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import back.StnPrsrController;
import back.TwitterController;

public class Main {
	
	private static TwitterController twtcntrl;
	private static StnPrsrController stnprsr;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		twtcntrl = new TwitterController();
		stnprsr = new StnPrsrController();
		//String test = "drumpf";
		
		//twtcntrl.searchTweetsWith(test);
		
		Tree parsedTree = stnprsr.parseString("The action story was not stale.");
		
		
		Collection<TypedDependency> dependencies = stnprsr.findDependencies(parsedTree);
		
		Vector<String[]> returned = extractAspects(dependencies);
		//Vector<String[]> returnew = complementWeb(dependencies);

		
		
		for(String[] results : returned)
		{
			System.out.println(results[0] +" - "+ results[1]+"\n");
		}
		System.out.println(dependencies);
	}
	
	
	private static Vector<String[]> extractAspects(Collection<TypedDependency> dependencies)
	{
		Vector<String[]> returned = new Vector<String[]>();
		Vector<String[]> temp;
		
		temp = adjectivalModifier(dependencies);
		returned.addAll(temp);
		temp = adverbialModifier(dependencies);
		returned.addAll(temp);
		temp = directObject(dependencies);
		returned.addAll(temp);
		temp = adjectivalComplement(dependencies);
		returned.addAll(temp);
		temp = adverbialModifierPssv(dependencies);
		returned.addAll(temp);
		temp = complementWeb(dependencies);
		returned.addAll(temp);
		
		return returned;
	}
	
	
	
	//ACHANDO AJETIVOS//
	
	//Adjectival modifier
	private static Vector<String[]> adjectivalModifier(Collection<TypedDependency> dependencies)
	{
		
		return containRelationUnaria(dependencies,"amod");	
	}
	
	//Adverbial modifier
	private static Vector<String[]> adverbialModifier(Collection<TypedDependency> dependencies)
	{
		
		return containRelationUnaria(dependencies,"advmod");	
	}
	
	//Direct object
	private static Vector<String[]> directObject(Collection<TypedDependency> dependencies)
	{
		return containRelationDupla(dependencies,"dobj","nsubj");
	}
	//Adjectival complement
	private static Vector<String[]> adjectivalComplement(Collection<TypedDependency> dependencies)
	{
		return containRelationDupla(dependencies,"xcomp","nsubj");
	}
	//Adverbial modifier to a passive verb
	private static Vector<String[]> adverbialModifierPssv(Collection<TypedDependency> dependencies)
	{
		return containRelationDupla(dependencies,"advmod","nsubjpass");
	}
	//Complement of a copular web
	private static Vector<String[]> complementWeb(Collection<TypedDependency> dependencies)
	{
		Vector<String[]> returnedFound = new Vector<String[]>();
		
		for(TypedDependency td:dependencies)
		{
			if(td.reln().toString().equals("nsubj")){
				for(TypedDependency td2:dependencies)
				{
					
					if(td2.reln().toString().equals("cop") && td.gov().equals(td2.gov()))
					{
						String[] tempRelation = new String[2];
						tempRelation[0] = compoundNoun(dependencies,td.dep().word())+td.dep().word();
						tempRelation[1] = compoundNoun(dependencies,td.gov().word())+simpleNeg(dependencies, td.gov().word());
						returnedFound.add(tempRelation);						
					}
				}
			}
		}
		return returnedFound;		
	}
	
	
	//Usado pelos outros
	//Compound noun
	private static String compoundNoun(Collection<TypedDependency> dependencies, String word)
	{
		String returned = "";
		for(TypedDependency td:dependencies)
		{
			if(td.gov().word() != null){
				if(td.gov().word().equals(word) && td.reln().toString().equals("compound"))
				{
					if(returned.equals(""))
					{
						returned += td.dep().word();
					}else{
						returned += " "+td.dep().word();
					}
				}
			}
		}
		if(returned.equals(""))
		{
			return returned;
		}else{
			return compoundNoun(dependencies,returned)+returned+" ";			
		}
	}
	
	//Simple negation
	private static String simpleNeg(Collection<TypedDependency> dependencies, String word)
	{
		String returned = "";
		for(TypedDependency td:dependencies)
		{
			if(td.gov().word() != null){
				if(td.gov().word().equals(word) && td.reln().toString().equals("neg"))
				{
					return "not "+word;
				}
			}
		}
		return word;
	}
	
	//Acha relacao un
	private static Vector<String[]> containRelationUnaria(Collection<TypedDependency> dependencies, String reln)
	{
		Vector<String[]> returnedFound = new Vector<String[]>();
		
		for(TypedDependency td:dependencies)
		{
			if(td.reln().toString().equals(reln)){
				String[] tempRelation = new String[2];
				tempRelation[0] = compoundNoun(dependencies,td.gov().word())+simpleNeg(dependencies, td.gov().word());
				tempRelation[1] = compoundNoun(dependencies,td.dep().word())+td.dep().word();
				returnedFound.add(tempRelation);
			}
		}
		return returnedFound;		
	}
	
	//Acha relacao dupla
	private static Vector<String[]> containRelationDupla(Collection<TypedDependency> dependencies,String reln1,String reln2)
	{
		Vector<String[]> returnedFound = new Vector<String[]>();
		
		for(TypedDependency td:dependencies)
		{
			if(td.reln().toString().equals(reln1)){
				for(TypedDependency td2:dependencies)
				{
					
					if(td2.reln().toString().equals(reln2) && td.gov().equals(td2.gov()))
					{
						String[] tempRelation = new String[2];
						tempRelation[0] = compoundNoun(dependencies, td.dep().word())+simpleNeg(dependencies, td.dep	().word());
						tempRelation[1] = compoundNoun(dependencies, td.dep().word())+td2.dep().word();
						returnedFound.add(tempRelation);						
					}
				}
			}
		}
		return returnedFound;		
	}
	
	
	//DEBUG ONLY//{
	private static void identifyAdjectiveModifier(Tree parsedTree)
	{
		
		List<Tree> leaves = parsedTree.getLeaves();
        // Print words and Pos Tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(parsedTree);
//            System.out.print(leaf.label().value() + "-" + parent.label().value() + " \n");
        }
	}
	
	private static void debugDependency(TypedDependency a){
		System.out.println("[Governor - "+a.gov()+" - Dependent - "+a.dep()+" - Relation - "+a.reln()+"]");
	}
	//DEBUG ONLY//}
	
}
