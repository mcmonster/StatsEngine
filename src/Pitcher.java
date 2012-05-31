import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Pitcher extends Player {
	public Pitcher() {
		VersusPlayerStats = new HashMap<String, Vector<Batter.Stats>>();
	}
	
	public Pitcher(String name, PositionType position, float salary) {
		super(name, position, salary);
		// TODO Auto-generated constructor stub
	}

	public Pitcher(Player player) {
		ID = player.ID;
		Name = player.Name;
		Position = player.Position;
		Salary = player.Salary;
		Team = player.Team;
		
		Games = new Vector<Integer>();
		for(int iter=0; iter<50; iter++)
			Games.add(new Integer(0));
		//TODO PitcherStats;
		YearStats = new Vector<Stats>();
		for(int iter=0; iter<50; iter++)
			YearStats.add(new Stats());
		VersusPlayerStats = new HashMap<String, Vector<Batter.Stats>>();
	}

	public void calcValue() {
		
		// Historical performance, generally speaking
		float historicalPerformance = calcHistoricalPerformance();
		
		// The versus team performance
		float versusTeamPerformance = calcVersusTeamPerformance();
		
		Value = (historicalPerformance+versusTeamPerformance)/2.0f;
	}

	public float calcHistoricalPerformance() {
		float retVal = calcAvgStat(YearStats, StatsType.WINS)*WIN_POINTS;
		retVal += calcAvgStat(YearStats, StatsType.EARNED_RUNS)*EARNED_RUN_POINTS;
		retVal += calcAvgStat(YearStats, StatsType.INNINGS_PITCHED)*INNING_PITCHED_POINTS;
		retVal += calcAvgStat(YearStats, StatsType.STRIKE_OUTS)*STRIKEOUT_POINTS;
		retVal += calcAvgStat(YearStats, StatsType.WALKS)*WALK_POINTS;
		retVal += calcAvgStat(YearStats, StatsType.HITS)*HIT_POINTS;
		retVal += calcAvgStat(YearStats, StatsType.COMPLETE_GAMES)*COMPLETE_GAME_POINTS;
		return retVal;
	}

	public float calcHitterRelevantPerformance(String playerName) {
	/*	Vector<Batter.Stats> stats = VersusPlayerStats.get(playerName);
		
		float retVal = calcAvgStat(stats, Batter.StatsType.HITS)*HITTER_HIT_POINTS;
		retVal += calcAvgStat(stats, StatsType.WALKS)*HITTER_WALK_POINTS;
		retVal += calcAvgStat(stats, StatsType.HOME_RUNS)*HITTER_HOMERUN_POINTS;
		retVal += calcAvgStat(stats, StatsType.EARNED_RUNS)*HITTER_RUN_POINTS;
		retVal += calcAvgStat(stats, StatsType.STRIKE_OUTS)*HITTER_STRIKEOUT_POINTS;
		return retVal / BATTERS_PER_GAME;
	*/
		return 0.0f;
	}
	
	public float calcVersusTeamPerformance() {
		float retVal = 0.0f;
		
		for(int playerIter=0; playerIter<CurrentTeam.size(); playerIter++) {
			String playerName = CurrentTeam.get(playerIter).getName();
			float hitterVal = calcHitterRelevantPerformance(playerName);
			
			// Determine historical number of at bats
			int atBatsVersusPitcher = 0;
			Vector<Batter.Stats> playerStats = VersusPlayerStats.get(playerName);
			for(int iter=0; iter<playerStats.size(); iter++)
				atBatsVersusPitcher += playerStats.get(iter).AtBats;
			
			if(atBatsVersusPitcher <= 7) retVal += 0.0f;
			else if(atBatsVersusPitcher <= 19) retVal += hitterVal*(atBatsVersusPitcher-7)/13.0f;
			else retVal += hitterVal;
		}
		
		return retVal / CurrentTeam.size();
	}
	
	public float calcAvgStat(final Vector<Stats> stats,
			                 final StatsType type) {
		float avg = 0.0f;
		
		for(int yearIter=0; yearIter<YearStats.size(); yearIter++) {
			int stat = 0;
			
			switch(type) {
				case HITS:
					stat = stats.get(yearIter).Hits;
					break;
				case WALKS:
					stat = stats.get(yearIter).Walks;
					break;
				case EARNED_RUNS:
					stat = stats.get(yearIter).Runs;
					break;
				case HOME_RUNS:
					stat = stats.get(yearIter).HomeRuns;
					break;
				case STRIKE_OUTS:
					stat = stats.get(yearIter).StrikeOuts;
					break;
				case COMPLETE_GAMES:
					stat = stats.get(yearIter).CompleteGames;
					break;
				case WINS:
					stat = stats.get(yearIter).Wins;
					break;
			}
		
			avg += stat/Games.get(yearIter)/(yearIter+1);
		}
		
		return avg;
	}
	
	public void parse() {
		try {
			// Pull the career stats page
			String url = "http://sports.yahoo.com/mlb/players/"+ID+"/career";
			Document careerStatsPage = Jsoup.connect(url).get();
			
			// Fetch all of the tables within the page and identify the Batting table
			Elements tables = careerStatsPage.getElementsByTag("table");
			int tableIter = -1;
			Element table = tables.get(tableIter+1);
			String innerHTML = "";
			do {
				try {
					tableIter++;
					table = tables.get(tableIter);
					innerHTML = table.child(0).child(0).child(0).html();
				} catch(IndexOutOfBoundsException ex) {continue;}
			} while(!innerHTML.equals("&nbsp;Pitching"));
			Element statsTable = tables.get(tableIter).child(0);
			
			// Parse each season
			int seasonIter = 2;
			do {
				try {
					Element currentSeason = statsTable.child(seasonIter++);
					String seasonIDString = currentSeason.child(0).html().split("\\;")[1];
					if(seasonIDString.equals("<b>Career</b>")) break;
					int seasonID = 2012-Integer.parseInt(seasonIDString);
					Stats currentStats = YearStats.get(seasonID);
					
					Games.set(seasonID, Integer.parseInt(currentSeason.child(2).html()));
					currentStats.Wins = Integer.parseInt(currentSeason.child(4).html());
					currentStats.CompleteGames = Integer.parseInt(currentSeason.child(9).html());
					currentStats.InningsPitched = (int)Float.parseFloat(currentSeason.child(11).html());
					currentStats.Hits = Integer.parseInt(currentSeason.child(12).html());
					currentStats.EarnedRuns = Integer.parseInt(currentSeason.child(13).html());
					currentStats.HomeRuns = Integer.parseInt(currentSeason.child(14).html());
					currentStats.Walks = Integer.parseInt(currentSeason.child(15).html());
					currentStats.StrikeOuts = Integer.parseInt(currentSeason.child(16).html());
					YearStats.set(seasonID, currentStats);
				} catch(IndexOutOfBoundsException ex) {break;}
			} while(seasonIter > 0);
			
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/*@Override
	public String[] parse(String[] rawText) {
		int season = 2011;
		String seasonString;

		// Throw away the header metadata
		while(!rawText[0].split(">")[0].equals("<season"))
			rawText = ArrayUtils.remove(rawText, 0);
		
		// Parse the season
		rawText[0] = rawText[0].replace("<season>", "");
		rawText[0] = rawText[0].replace("</season>", "");
		season -= Integer.parseInt(rawText[0]);
		seasonString = rawText[0];
		rawText = ArrayUtils.remove(rawText, 0);
		
		System.out.println("Scrapping versus stats for player '"+ID+"', year '"+seasonString+"'");
		scrapBatterVsPitcherStats(ID, (season+2011));
		
		// Remove the stats tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Parse the stats
		while(!rawText[0].split(">")[0].equals("</stats")) {
			// Remove the stat tag
			rawText = ArrayUtils.remove(rawText, 0);
			
			// Get the stat id
			rawText[0] = rawText[0].replace("<stat_id>", "");
			rawText[0] = rawText[0].replace("</stat_id>", "");
			int rawStatType = Integer.parseInt(rawText[0]);
			StatsType type = StatsType.parse(rawStatType);
			rawText = ArrayUtils.remove(rawText, 0);
			
			// Get the stat value
			rawText[0] = rawText[0].replace("<value>", "");
			rawText[0] = rawText[0].replace("</value>", "");
			int statValue = 0;
			boolean notNull = (type != null);
			boolean notHyphen = (!rawText[0].equals("-"));
			if(notNull && notHyphen) {
				if(type == StatsType.INNINGS_PITCHED)
					statValue = (int)Float.parseFloat(rawText[0]); 
				else
					statValue = Integer.parseInt(rawText[0]);
			}
			rawText = ArrayUtils.remove(rawText, 0);
		
			if(type != null) {
				Stats stats = YearStats.get(season);
				switch(type) {
					case GAMES_PLAYED:
						Games.set(season, statValue);
						break;
					case HOME_RUNS:
						stats.HomeRuns = statValue;
						break;
					case WALKS:
						stats.Walks = statValue;
						break;
					case COMPLETE_GAMES:
						stats.CompleteGames = statValue;
						break;
					case EARNED_RUNS:
						stats.EarnedRuns = statValue;
						break;
					case HITS:
						stats.Hits = statValue;
						break;
					case INNINGS_PITCHED:
						stats.InningsPitched = statValue;
						break;
					case STRIKE_OUTS:
						stats.StrikeOuts = statValue;
						break;
					case WINS:
						stats.Wins = statValue;
						break;
					default:
						System.err.println("Unknown Stat Category: "+rawStatType);
						break;
				}
				YearStats.set(season, stats);
			}
			
			// Remove the /stat tag
			rawText = ArrayUtils.remove(rawText, 0);
		}
		
		return rawText;
	}*/
	
	public class Stats {
		public int AtBats;
		public int EarnedRuns;
		public int Hits;
		public float InningsPitched;
		public int Walks;
		public int Runs;
		public int HomeRuns;
		public int StrikeOuts;
		public int Wins;
		public int CompleteGames;
	}
	
	public enum StatsType {
		COMPLETE_GAMES("Complete Games"),
		EARNED_RUNS("Earned Runs"),
		GAMES_PLAYED("Games Played"),
		HITS("Hits"),
		INNINGS_PITCHED("Innings Pitched"),
		WALKS("Walks"),
		HOME_RUNS("Home Runs"),
		STRIKE_OUTS("Strike Outs"),
		WINS("Wins");
		
		private StatsType(String value) {
			Value = value;
		}
		
		@Override
		public String toString() {
			return Value;
		}
		
		public static StatsType parse(int id) {
			switch(id) {
				case 0: 
				case 1:
					return GAMES_PLAYED;
				case 39:
					return WALKS;
				case 42:
					return STRIKE_OUTS;
				case 28:
					return WINS;
				case 30:
					return COMPLETE_GAMES;
				case 34:
					return HITS;
				case 37:
					return EARNED_RUNS;
				case 38:
					return HOME_RUNS;
				case 50:
					return INNINGS_PITCHED;
				default:
					//System.err.println("Unknown Stat Category: "+id);
					return null;
			}
		}
		
		private String Value;
	}
	
	public void scrapBatterVsPitcherStats(int playerID, int year) {
		try {
			// Grab the batter vs pitcher stats page
			Document doc = Jsoup.connect("http://sports.yahoo.com/mlb/players/"+playerID+"/batvspit").get();
			
			Elements links = doc.getElementsByTag("a");
			int count = 0;
			Element link = links.get(count);
			String innerLinkHTML = "";
			do {
				try {
					link = links.get(count);
					innerLinkHTML = link.html();
					count++;
				} catch(IndexOutOfBoundsException ex) {
					continue;
				}
			} while (!innerLinkHTML.equals("Career"));
			doc = Jsoup.connect("http://sports.yahoo.com/"+link.attr("href")).get();
			links = doc.getElementsByTag("a");
			count = 0;
			link = links.get(count);
			innerLinkHTML = "";
			do {
				try {
					link = links.get(count);
					innerLinkHTML = link.html();
					count++;
				} catch(IndexOutOfBoundsException ex) {
					continue;
				}
			} while (!innerLinkHTML.equals(Integer.toString(year)));
			doc = Jsoup.connect("http://sports.yahoo.com/"+link.attr("href")).get();
			
			String[] teams = {"&nbsp;vs. Arizona Diamondbacks",
							  "&nbsp;vs. Atlanta Braves",
							  "&nbsp;vs. Baltimore Orioles",
							  "&nbsp;vs. Boston Red Sox",
							  "&nbsp;vs. Chicago Cubs",
							  "&nbsp;vs. Chicago White Sox",
							  "&nbsp;vs. Cincinnati Reds",
							  "&nbsp;vs. Cleveland Indians",
							  "&nbsp;vs. Colorado Rockies",
							  "&nbsp;vs. Detroit Tigers",
							  "&nbsp;vs. Houston Astros",
							  "&nbsp;vs. Kansas City Royals",
							  "&nbsp;vs. Los Angeles Angels",
							  "&nbsp;vs. Los Angeles Dodgers",
							  "&nbsp;vs. Miami Marlins",
							  "&nbsp;vs. Milwaukee Brewers",
							  "&nbsp;vs. Minnesota Twins",
							  "&nbsp;vs. New York Mets",
							  "&nbsp;vs. New York Yankees",
							  "&nbsp;vs. Oakland Athletics",
							  "&nbsp;vs. Philadelphia Phillies",
							  "&nbsp;vs. Pittsburgh Pirates",
							  "&nbsp;vs. San Diego Padres",
							  "&nbsp;vs. San Francisco Giants",
							  "&nbsp;vs. Seattle Mariners",
							  "&nbsp;vs. St. Louis Cardinals",
							  "&nbsp;vs. Tampa Bay Rays",
							  "&nbsp;vs. Texas Rangers",
							  "&nbsp;vs. Toronto Blue Jays",
							  "&nbsp;vs. Washington Nationals",
							  ""};
			
			for(int teamIter=0; teamIter<teams.length; teamIter++) {
				// Get the team based blocks of data
				Elements teamBlocks = doc.getElementsByTag("table");
				count = -1;
				String innerHTML = "";
				do {
					count++;
					try {
						Element div = teamBlocks.get(count);
						Element child = div.child(0);
						if(child != null) {
							Element child2 = child.child(0);
							if(child2 != null) {
								Element child3 = child2.child(0);
								innerHTML = child3.html();
							}
						}
					} catch(IndexOutOfBoundsException ex) {
						break;
					}
				} while(!innerHTML.equals(teams[teamIter]));
				Element currentTeam;
				try {
					currentTeam = teamBlocks.get(count).child(0);
				} catch(IndexOutOfBoundsException ex) {
					continue;
				}
					
				int childCount = 2;
				do {
					try {
						Element currentPlayer = currentTeam.child(childCount++);
						String playerName = currentPlayer.child(0).child(0).html();
						if(playerName.split("\\&").length > 1) {
							String temp = playerName.split("\\&")[0]+playerName.split("\\&")[1].charAt(0);
							playerName = temp + playerName.split("\\;")[1];
						}
						
						if(!VersusPlayerStats.containsKey(playerName)) {
							Vector<Batter.Stats> stats = new Vector<Batter.Stats>();
							for(int iter=0; iter<12; iter++)
								stats.add(Batter.constructStats());
							VersusPlayerStats.put(playerName, stats);
						}
						Batter.Stats stats = VersusPlayerStats.get(playerName).get(2012-year);
						stats.AtBats = Integer.parseInt(currentPlayer.child(1).html());
						stats.Doubles = Integer.parseInt(currentPlayer.child(3).html());
						stats.Triples = Integer.parseInt(currentPlayer.child(4).html());
						stats.HomeRuns = Integer.parseInt(currentPlayer.child(5).html());
						stats.RBIs = Integer.parseInt(currentPlayer.child(6).html());
						stats.Walks = Integer.parseInt(currentPlayer.child(7).html());
						stats.Outs = Integer.parseInt(currentPlayer.child(8).html());
						stats.StolenBases = Integer.parseInt(currentPlayer.child(9).html());
						
						stats.Singles = Integer.parseInt(currentPlayer.child(2).html());
						stats.Singles -= stats.Doubles + stats.Triples + stats.HomeRuns;
					} catch(IndexOutOfBoundsException ex) {
						break;
					}
				} while(childCount > 0);
			}
		} catch(Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private Vector<Player>				CurrentTeam;
	private final static int   			BATTERS_PER_GAME = 9;
	private final static float 			COMPLETE_GAME_POINTS = 2.0f;
	private final static float 			EARNED_RUN_POINTS = -1.0f;
	private Vector<Integer>    			Games;
	private final static float 			HIT_POINTS = -0.25f;
	private final static float 			HITTER_HIT_POINTS = 1.5f;
	private final static float 			HITTER_HOMERUN_POINTS = 4.0f;
	private final static float 			HITTER_RUN_POINTS = 1.0f;
	private final static float 			HITTER_STRIKEOUT_POINTS = -0.25f;
	private final static float 			HITTER_WALK_POINTS = 1.0f;
	private final static float 			INNING_PITCHED_POINTS = 0.75f;
	private final static float			STRIKEOUT_POINTS = 0.75f;
	private Map<String, Vector<Batter.Stats> > VersusPlayerStats;
	private final static float 		 	WALK_POINTS = -0.25f;
	private final static float 			WIN_POINTS = 2.0f;
	private Vector<Stats>      			YearStats;
}
