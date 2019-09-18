package loderunner.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import loderunner.services.Cell;
import loderunner.services.CellContent;
import loderunner.services.Command;
import loderunner.services.EditableScreenService;
import loderunner.services.EngineService;
import loderunner.services.EnvironmentService;
import loderunner.services.GuardService;
import loderunner.services.ItemService;
import loderunner.services.ItemType;
import loderunner.services.Pair;
import loderunner.services.PlayerService;
import loderunner.services.Status;
import loderunner.services.Triplet;


public class EngineImpl implements EngineService {
	
	EnvironmentService envi;
	PlayerService player;
	ArrayList<GuardService> guards;
	HashSet<ItemService> treasures;
	Status status;
	EditableScreenService screen;
	int x; 
	int y;
	List<Pair<Integer, Integer>> listGuards;
	List<Pair<Integer, Integer>> listTresors;
	ArrayList<Triplet<Integer,Integer,Integer>> holes;
	
	Command nextCommand;

	
	
	/*
	 * Pour le test  
	 */
	public void setCmd(Command c) {
		nextCommand = c;
	}
	
	@Override
	public EnvironmentService getEnvironment() {
		return envi;
	}

	@Override
	public PlayerService getPlayer() {
		return player;
	}

	@Override
	public ArrayList<GuardService> getGuards() {
		return guards;
	}

	@Override
	public Set<ItemService> getTreasures() {

		return treasures;
	}

	@Override
	public Command getNextCommand() {
//		String command ;
//		Scanner scanIn = new Scanner(System.in);
//		
//		command = scanIn.nextLine();
//		
//		switch(command) {
//		case "d" : return Command.Right;
//		case "q" : return Command.Left;
//		case "z" : return Command.Up;
//		case "s" : return Command.Down;
//		case "o" : return Command.DigL;
//		case "p" : return Command.DigR;
//		default :  return Command.Neutral;
//		}
//		
		return nextCommand;
		
	}

	public Status getStatus() {
		return status;
	}



	@Override
	public void step() {
		
		//Si au debut d’un tour, le joueur se trouve sur une case contenant un tresor,
		//ce tresor disparait
		int x = player.getWdt();
		int y = player.getHgt();
		
		Set<CellContent> set = envi.getCellContent(x, y);
		for(CellContent c :set) {
			if (c instanceof ItemService ) {
				set.remove(c);
				treasures.remove(c);
			}
		}
		
		//	le temps de chaque trou est incrementee
		for(Triplet<Integer, Integer, Integer> h : holes) {
			int t = h.getThird();
			if(t<15) {
				h.setThird(t+1);
			}
		}
		
	
		player.step();
		
//		for(GuardService guard : guards) {
//			guard.step();
//			
//		}
		
	//	tous les trous dont la troisieme coordonnees vaut 15 sont rebouches.
		
		for(Triplet<Integer, Integer, Integer> h : holes) {
			int t = h.getThird();
			if(t==15) {
				
				
				screen.setNature(h.getFirst(), h.getSecond(), Cell.PLT);
				
				//	si le joueur etait dedans
				if(h.getFirst() == player.getWdt() && h.getSecond() == player.getHgt()) {
					//	le jeu est perdu
					status = Status.Loss;
				}
				
				//si un garde était dedans, il revient a sa position initiale
				for(GuardService guard : guards) {	
					if(h.getFirst() == guard.getWdt() && h.getSecond() == guard.getHgt()) {
						int index = guards.indexOf(guard);
						int guardX_init = listGuards.get(index).getL();
						int guardY_init = listGuards.get(index).getR();
						guard.init(screen, guardX_init, guardY_init);
					}
					
				}
				
			}
		}
		
		
		
		//	Le jeu est gagné quand il n’y a plus de trésors.
		if(treasures.size() == 0) {			
			status = Status.Win;
		}
		
		
	}

	@Override
	public ArrayList<Triplet<Integer, Integer, Integer>> getHoles() {
		
		return holes;
	}

	@Override
	public void init(EditableScreenService screen, int x, int y, List<Pair<Integer, Integer>> listGuards,
			List<Pair<Integer, Integer>> listTresors) {
		
		
		
		holes = new ArrayList<Triplet<Integer,Integer,Integer>>();
		int id =0;
		
		//créer un environment
		envi = new EnvironmentImpl(screen.getHeight(),screen.getWidth());
		envi.init(screen);
		
		
		
		player = new PlayerImpl();
		player.init(envi, x, y);
		player.init(this);
		envi.getCellContent(x, y).add(player);
		for(Pair<Integer,Integer> l : listGuards) {
			// pas de gardes 
		}
		
		treasures = new HashSet<ItemService>();
		for(Pair<Integer,Integer> l : listTresors) {
			 ItemImpl tresor = new ItemImpl(id, ItemType.Treasure, l.getL(), l.getR());
			treasures.add(tresor);
			id++;
			envi.getCellContent(l.getL(), l.getR()).add(tresor);
		}
		
	}

	
	

}
