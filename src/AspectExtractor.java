
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;


public class AspectExtractor {
	
	public AspectExtractor(){}
	
	public static Vector<String[]> extractAspects(Collection<TypedDependency> dependencies)
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
		
		return containRelationUnaria(dependencies,"amod","NN","JJ");	
	}
	
	//Adverbial modifier
	private static Vector<String[]> adverbialModifier(Collection<TypedDependency> dependencies)
	{
		
		return containRelationUnaria(dependencies,"advmod","JJ","NN");	
	}
	
	//Direct object
	private static Vector<String[]> directObject(Collection<TypedDependency> dependencies)
	{
		return containRelationDupla(dependencies,"dobj","nsubj","JJ","NN");
	}
	//Adjectival complement
	private static Vector<String[]> adjectivalComplement(Collection<TypedDependency> dependencies)
	{
		return containRelationDupla(dependencies,"xcomp","nsubj","JJ","NN");
	}
	//Adverbial modifier to a passive verb
	private static Vector<String[]> adverbialModifierPssv(Collection<TypedDependency> dependencies)
	{
		return containRelationDupla(dependencies,"advmod","nsubjpass","RB","NN");
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
					
					if(td2.reln().toString().equals("cop") && td.gov().equals(td2.gov()) && td.dep().tag().toString().equals("NN") && td2.dep().tag().toString().equals("VBD"))
					{
						//debugDependency(td);
						//debugDependency(td2);
						
						String[] tempRelation = new String[2];
						tempRelation[1] = compoundNoun(dependencies,td.dep().word()) + td.dep().word();
						tempRelation[0] = simpleNeg(dependencies, td.gov().word())+adverbialModsModifier(dependencies,td.gov().word(),true)+td.gov().word();
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
		return returned;
	}
	
	//Adverbial Modifier
	private static String adverbialModsModifier(Collection<TypedDependency> dependencies, String word, boolean compound)
	{
		String returned = "";
		for(TypedDependency td:dependencies)
		{
			if(!compound){
				if(td.dep().word() != null){
					if(td.dep().word().equals(word) && td.reln().toString().equals("advmod"))
					{
						returned += " "+td.gov().word().toString();
					}
				}
			}else{
				if(td.gov().word() != null){
					if(td.gov().word().equals(word) && td.reln().toString().equals("advmod"))
					{
						returned += td.dep().word().toString()+" ";
					}
				}
			}
		}
		return returned;
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
					return "not ";
				}
			}
		}
		return negThroughNo(dependencies, word);
	}
	
	//Negation through no determiner
	private static String negThroughNo(Collection<TypedDependency> dependencies, String word)
	{
		for(TypedDependency td:dependencies)
		{
			if(td.dep().word()!=null)
			{
				if(td.dep().word().equals(word) && td.reln().toString().equals("amod"))
				{
					for(TypedDependency td2:dependencies)
					{
						if(td2.gov().word()!=null)
						{
							if(td2.gov().word().equals(td.gov().word()) && td2.dep().word().equals("no"))
							{
								return "not ";						
							}
						}
					}
				}
			}
		}
		return negThroughHyp(dependencies, word);
	}
	
	private static String negThroughHyp(Collection<TypedDependency> dependencies, String word)
	{
		int auxs = 0;
		boolean cop = false;
		for(TypedDependency td:dependencies)
		{
			if(td.gov().word() != null){
				if(td.gov().word().equals(word))
				{
					if(td.reln().toString().equals("aux"))
					{
						auxs++;
					}
					if(td.reln().toString().equals("cop"))
					{
						cop = true;
					}
				}
			}
		}
		if(auxs==2 && cop)
		{
			
			return "not ";
		}
		return "";
	} 
	
	//Acha relacao un
	private static Vector<String[]> containRelationUnaria(Collection<TypedDependency> dependencies, String reln,String govType,String depType)
	{
		Vector<String[]> returnedFound = new Vector<String[]>();
		
		for(TypedDependency td:dependencies)
		{
			if(td.reln().toString().equals(reln) && td.gov().tag().toString().equals(govType) && td.dep().tag().toString().equals(depType)){
				String[] tempRelation = new String[2];
				tempRelation[1] = compoundNoun(dependencies,td.gov().word())+td.gov().word();
				tempRelation[0] = simpleNeg(dependencies, td.dep().word())+td.dep().word()+adverbialModsModifier(dependencies,td.dep().word(),false);
				returnedFound.add(tempRelation);
			}
		}
		return returnedFound;		
	}
	
	//Acha relacao dupla
	private static Vector<String[]> containRelationDupla(Collection<TypedDependency> dependencies,String reln1,String reln2,String depAsp,String depModi)
	{
		Vector<String[]> returnedFound = new Vector<String[]>();
		
		for(TypedDependency td:dependencies)
		{
			if(td.reln().toString().equals(reln1)){
				for(TypedDependency td2:dependencies)
				{
					
					if(td2.reln().toString().equals(reln2) && td.gov().equals(td2.gov()) && td.dep().tag().toString().equals(depAsp) && td2.dep().tag().toString().equals(depModi))
					{
						//debugDependency(td);
						//debugDependency(td2);
						String[] tempRelation = new String[2];
						tempRelation[0] = simpleNeg(dependencies, td.gov().word())+td.dep().word()+adverbialModsModifier(dependencies,td.dep().word(),false);
						tempRelation[1] = compoundNoun(dependencies, td2.dep().word())+td2.dep().word();
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
	
	public static void debugDependency(TypedDependency a){
		System.out.println("[Governor - "+a.gov()+"["+a.gov().tag()+"] - Dependent - "+a.dep()+"["+a.dep().tag()+"] - Relation - "+a.reln()+"]");
	}
	//DEBUG ONLY//}
}
