import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;

public class Populator {
	public static Vector<Player> populatePlayers() {
		StatFetcher    fetcher = new StatFetcher();
		Vector<Player> players = new Vector<Player>();
		
		try {
			boolean endOfStream = false;
			int startCount = 0;
			
			do {
				Thread.sleep(2000);
				System.out.println("Fetching player metadata "+startCount+" - "+(startCount+25));
				String request = "http://fantasysports.yahooapis.com/fantasy/v2/game/253/players;start=";
				request+=startCount;
				
				// Fetch list of player IDs for all pitchers for last year
				String[] results = fetcher.fetch(request).split("\n");
				for(int iter=0; iter<results.length; iter++)
					results[iter] = results[iter].trim();

				// Throw away the header metadata
				while((!results[0].equals("<player>") && (!results[0].equals("<players/>"))))
					results = ArrayUtils.remove(results, 0);
				
				if(results[0].equals("<players/>")) break;
				
				while(!results[0].equals("</players>")) {
					Player player = new Player();
					results = player.parse(results);
					players.add(player);
				}
				
				startCount+=25;
				if(startCount==25) break;
			} while(!endOfStream);
			
			for(int playerIter=0; playerIter<players.size(); playerIter++) {
				/*for(int yearIter=0; yearIter<11; yearIter++) {
					Thread.sleep(1000);
					Player player = players.get(playerIter);
					System.out.println("Fetching player stats "+player.getName()+" "+(2011-yearIter));
					
					// Fetch the data
					String request = "http://fantasysports.yahooapis.com/fantasy/v2/player/";
					switch(yearIter) {
						case 0:
							request+="253";
							break;
						case 1:
							request+="238";
							break;
						case 2:
							request+="215";
							break;
						case 3:
							request+="195";
							break;
						case 4:
							request+="171";
							break;
						case 5:
							request+="147";
							break;
						case 6:
							request+="113";
							break;
						case 7:
							request+="98";
							break;
						case 8:
							request+="74";
							break;
						case 9:
							request+="39";
							break;
						case 10:
							request+="12";
							break;
						
					}
					request+=".p."+player.getID()+"/stats";
					String[] rawText = fetcher.fetch(request).split("\n");
					for(int iter=0; iter<rawText.length; iter++)
						rawText[iter] = rawText[iter].trim();
					
					if(player.getPosition() ==  Player.PositionType.PITCHER) {
						Pitcher pitcher = new Pitcher(player);
						pitcher.parse(rawText);
						players.set(playerIter, pitcher);
					} else {
						Batter batter = new Batter(player);
						batter.parse(rawText);
						players.set(playerIter, batter);
					}
				}*/
				Player player = players.get(playerIter);
				System.out.println("Fetching player stats #"+playerIter+" "+player.getName());
				if(player.getPosition() == Player.PositionType.PITCHER) {
					Pitcher pitcher = new Pitcher(player);
					pitcher.parse();
					players.set(playerIter, pitcher);
				} else {
					Batter batter = new Batter(player);
					batter.parse();
					players.set(playerIter, batter);
				}
			}
		} catch(Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		return players;
	}
	
	public static void main(String[] args) {
		Vector<Player> players = Populator.populatePlayers();
		for(int iter=0; iter<players.size(); iter++) {
			Player player = players.get(iter);
			if(player.getPosition() ==  Player.PositionType.PITCHER) {
				if(player instanceof Pitcher) {
					Pitcher pitcher = new Pitcher(player);
					pitcher.calcValue();
					System.out.println(pitcher.getName()+" = "+pitcher.getValue());
				}
			} else {
				if(player instanceof Batter) {
					Batter batter = (Batter)player;
					batter.calcValue();
					System.out.println(batter.getName()+" = "+batter.getValue());
				}
			}
		}
	}
}
