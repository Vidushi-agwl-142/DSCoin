package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.MerkleTree;
import HelperClasses.TreeNode;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;
  public int last_in_process;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Pair<String, TransactionBlock> coin = mycoins.remove(0); 
    Transaction tobj = new Transaction();
    tobj.coinID = coin.first;
    tobj.Source = this;
    int s = 0;
    for (int i=0 ; i<DSobj.memberlist.length && s==0 ; i++){
      if(destUID.equals(DSobj.memberlist[i].UID)){
        s=1; tobj.Destination = DSobj.memberlist[i];
      }
    }
    tobj.coinsrc_block = coin.second;
    if(in_process_trans[0]==null){
        last_in_process=0;
    }
    else{
        last_in_process++;
    }
    in_process_trans[last_in_process] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
      TransactionBlock curr = DSObj.bChain.lastBlock; boolean found = false; int index = 0;
      while(!found && curr!=null){
          Transaction[] arr = curr.trarray;
          for(int j=0 ; j<arr.length && !found ; ++j){
              if(arr[j]==tobj){
                  found = true; index = j;
              }
      }
      if(!found){curr=curr.previous;}
      }
      if(!found){throw new MissingTransactionException();}
      else{
        MerkleTree m = curr.Tree;
        List<Pair<String,String>> path = new ArrayList<Pair<String,String>>();
        List<Pair<String,String>> final_path = new ArrayList<Pair<String,String>>();
	    Pair<String,String> sibling = new Pair<String,String>(m.rootnode.val,null);
	    path.add(sibling);
	    int curr_idx = index+1;
	    int num_nodes = curr.trarray.length;
	    TreeNode curr_node = m.rootnode;
	    while(num_nodes>1){
	        sibling = new  Pair<String,String>(curr_node.left.val,curr_node.right.val);
		    path.add(sibling);
		    if(curr_idx <= num_nodes/2){
		        curr_node = curr_node.left;
		    }
		    else{
		        curr_node = curr_node.right;
		        curr_idx -= num_nodes/2;
		    }
		    num_nodes /= 2;
	    }
	    for(int i=path.size()-1; i>=0; i-=1) {
            Pair<String, String> p = path.get(i);
            final_path.add(p);
        }
        List<Pair<String,String>> path1 = new ArrayList<Pair<String,String>>();
        List<Pair<String,String>> final_path1 = new ArrayList<Pair<String,String>>();
        TransactionBlock curr_block = DSObj.bChain.lastBlock;
        Pair<String,String> dgst_proof;
        while(curr_block!=curr){
          dgst_proof = new Pair<String,String>(curr_block.dgst, curr_block.previous.dgst+"#"+curr_block.trsummary+"#"+curr_block.nonce);
          path1.add(dgst_proof); curr_block=curr_block.previous;
        }
        if (curr_block.previous != null) {
            dgst_proof = new Pair<String,String>(curr_block.dgst, curr_block.previous.dgst+"#"+curr_block.trsummary+"#"+curr_block.nonce);
            path1.add(dgst_proof);
            dgst_proof = new Pair<String,String>(curr_block.previous.dgst, null);
            path1.add(dgst_proof);
        }
        else{
            dgst_proof = new Pair<String,String>(curr_block.dgst, "DSCoin"+"#"+curr_block.trsummary+"#"+curr_block.nonce);
            path1.add(dgst_proof);
            dgst_proof = new Pair<String,String>("DSCoin", null);
            path1.add(dgst_proof);
        }
        for(int i=path1.size()-1; i>=0; i--){
		Pair<String,String> p = path1.get(i);
		final_path1.add(p);
	    }
        Pair<List<Pair<String, String>>, List<Pair<String, String>>> ans = new Pair<>(final_path,final_path1);
        Transaction[] new_process_transaction = new Transaction[100];
        int j=0; last_in_process--;
        for(int i=0 ; i<100 ; ++i){
          if(in_process_trans[i]!=tobj){
            new_process_transaction[j]=in_process_trans[i]; j++;
          }
        }
        in_process_trans = new_process_transaction;
        List<Pair<String, TransactionBlock>> mycoins_new = new ArrayList<>();
        int i=0; boolean bool = false;
        while(i<tobj.Destination.mycoins.size()){
          if(!bool && tobj.coinID.compareTo(tobj.Destination.mycoins.get(i).first)<0){
            Pair<String, TransactionBlock> p = new Pair<String, TransactionBlock>(tobj.coinID, curr);
            mycoins_new.add(p);
            mycoins_new.add(tobj.Destination.mycoins.get(i));
            bool = true;
          }
          else{
            mycoins_new.add(tobj.Destination.mycoins.get(i));
          }
          i++;
        }
        tobj.Destination.mycoins=mycoins_new;
        return ans;
    }
  }

  public void MineCoin(DSCoin_Honest DSObj) {
      int count = 0; Transaction t = new Transaction();
      int n = DSObj.bChain.tr_count; TransactionBlock lB = DSObj.bChain.lastBlock;
      Transaction[] arr = new Transaction[n];
      while(count<n-1 && DSObj.pendingTransactions.firstTransaction!=null){
          try{
              t=DSObj.pendingTransactions.RemoveTransaction();
              if(lB.checkTransaction(t)) {
                  System.out.println("hi");

                  boolean found = false;
                  for (Transaction tr : arr) {
                      if (tr != null) {
                          if (tr.coinID.equals(t.coinID)) {
                              found = true;
                          }
                      }
                  }
                  if (!found) {
                      arr[count] = t;
                      count++;
                  }
              }
          }
          catch(EmptyQueueException en){
              System.out.println("transactions are not sufficient for mining");
          }
      }
      if(count==n-1){
          t = new Transaction(); int i = Integer.valueOf(DSObj.latestCoinID)+1;
          DSObj.latestCoinID = Integer.toString(i);
          t.coinID=DSObj.latestCoinID;
          t.Source=null;
          t.Destination=this;
          t.coinsrc_block=null;
          arr[n-1]=t;
          TransactionBlock tB = new TransactionBlock(arr);
          DSObj.bChain.InsertBlock_Honest(tB);
          Pair<String, TransactionBlock> p = new Pair<>(t.coinID,tB);
          mycoins.add(p);
      }
      else{
          System.out.println(count+"Atransactions are not sufficient for mining");
      }
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
      int count = 0; Transaction t = new Transaction();
      int n = DSObj.bChain.tr_count; TransactionBlock lB = DSObj.bChain.FindLongestValidChain();
      Transaction[] arr = new Transaction[n];
      while(count<n-1 && DSObj.pendingTransactions.firstTransaction!=null){
          try{
              t=DSObj.pendingTransactions.RemoveTransaction();
              if(lB.checkTransaction(t)) {
                  boolean found = false;
                  for (Transaction tr : arr) {
                      if (tr != null) {
                          if (tr.coinID.equals(t.coinID)) {
                              found = true;
                          }
                      }
                  }
                  if (!found) {
                      arr[count] = t;
                      count++;
                  }
              }
          }
          catch(EmptyQueueException en){
              System.out.println("transactions are not sufficient for mining");
          }
      }
      if(count==n-1){
          t = new Transaction(); int i = Integer.valueOf(DSObj.latestCoinID)+1;
          DSObj.latestCoinID = Integer.toString(i);
          t.coinID=DSObj.latestCoinID;
          t.Source=null;
          t.Destination=this;
          t.coinsrc_block=null;
          arr[n-1]=t;
          TransactionBlock tB = new TransactionBlock(arr);
          DSObj.bChain.InsertBlock_Malicious(tB);
          Pair<String, TransactionBlock> p = new Pair<>(t.coinID,tB);
          mycoins.add(p);
      }
      else{
          System.out.println("transactions are not sufficient for mining");
      }
  }  
}
