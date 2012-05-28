import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Batter extends Player {
	public Batter() {}
	
	public Batter(String name, PositionType position, float salary) {
		super(name, position, salary);
	}

	public Batter(Player player) {
		ID = player.ID;
		Name = player.Name;
		Position = player.Position;
		Salary = player.Salary;
		Team = player.Team;
		
		CurrentPitcher = null;
		Games = new Vector<Integer>();
		for(int iter=0; iter<50; iter++)
			Games.add(new Integer(0));
		//TODO PitcherStats;
		YearStats = new Vector<Stats>();
		for(int iter=0; iter<50; iter++)
			YearStats.add(new Stats());
	}

	public void calcValue() {
		float versusWeight = 0.0f;
		
		//int atBatsVersusPitcher = PitcherStats.get(CurrentPitcher.getName()).AtBats;
		//if(atBatsVersusPitcher <= 7) versusWeight = 0.0f;
		//else if(atBatsVersusPitcher <= 19) versusWeight = (atBatsVersusPitcher-7)/13.0f/3.0f;
		//else versusWeight = 1.0f/3.0f;
		
		// Historical performance, generally speaking
		float historicalPerformance = calcHistoricalPerformance();
		System.out.println(Name+" = "+historicalPerformance);
		System.out.println("Meow");
		
		// Performance vs pitcher
		//float versusPitcherPerformance = calcVersusPitcherPerformance();
		
		// Performance of pitcher
		//float pitcherPerformance = CurrentPitcher.calcHitterRelevantPerformance(Name);
		
		//Value = ((historicalPerformance*(1.0f-versusWeight)/2.0f)+
		//		(versusPitcherPerformance*versusWeight)+
		//		(pitcherPerformance*(1.0f-versusWeight)/2.0f));
	} 

	public float calcHistoricalPerformance() {
		float avgHomeRuns = calcAvgStat(StatsType.HOME_RUNS);
		float avgRBIs = calcAvgStat(StatsType.RBIS);
		float avgSingles = calcAvgStat(StatsType.SINGLES);
		float avgDoubles = calcAvgStat(StatsType.DOUBLES);
		float avgTriples = calcAvgStat(StatsType.TRIPLES);
		float avgRuns = calcAvgStat(StatsType.RUNS);
		float avgWalks = calcAvgStat(StatsType.WALKS);
		float avgStolenBases = calcAvgStat(StatsType.STOLEN_BASES);
		float avgOuts = calcAvgStat(StatsType.STRIKEOUTS);
		
		float retVal = avgHomeRuns*HOME_RUN_POINTS;
		retVal += avgRBIs*RBI_POINTS;
		retVal += avgSingles*SINGLE_POINTS;
		retVal += avgDoubles*DOUBLE_POINTS;
		retVal += avgTriples*TRIPLE_POINTS;
		retVal += avgRuns*RUN_POINTS;
		retVal += avgWalks*WALK_POINTS;
		retVal += avgStolenBases*STOLEN_BASE_POINTS;
		retVal += avgOuts*OUT_POINTS;
		
		return retVal;
	}
	
	public float calcVersusPitcherPerformance() {
		Stats stats = PitcherStats.get(CurrentPitcher.getName());
		
		float retVal = stats.HomeRuns*HOME_RUN_POINTS;
		retVal += stats.RBIs*RBI_POINTS;
		retVal += stats.Singles*SINGLE_POINTS;
		retVal += stats.Doubles*DOUBLE_POINTS;
		retVal += stats.Triples*TRIPLE_POINTS;
		retVal += stats.Runs*RUN_POINTS;
		retVal += stats.Walks*WALK_POINTS;
		retVal += stats.StolenBases*STOLEN_BASE_POINTS;
		retVal += stats.Outs*OUT_POINTS;
		return retVal;
	}
	
	public float calcAvgStat(final StatsType type) {
		float avg = 0.0f;
		
		for(int yearIter=0; yearIter<YearStats.size(); yearIter++) {
			int stat = 0;
			
			switch(type) {
				case DOUBLES:
					stat = YearStats.get(yearIter).Doubles;
					break;
				case SINGLES:
					stat = YearStats.get(yearIter).Singles;
					break;
				case TRIPLES:
					stat = YearStats.get(yearIter).Triples;
					break;
				case HOME_RUNS:
					stat = YearStats.get(yearIter).HomeRuns;
					break;
				case RBIS:
					stat = YearStats.get(yearIter).RBIs;
					break;
				case RUNS:
					stat = YearStats.get(yearIter).Runs;
					break;
				case STOLEN_BASES:
					stat = YearStats.get(yearIter).StolenBases;
					break;
				case STRIKEOUTS:
					stat = YearStats.get(yearIter).Outs;
					break;
				case WALKS:
					stat = YearStats.get(yearIter).Walks;
					break;
			}
		
			if(Games.get(yearIter) > 0) {
				float currentYearAvg = (float)stat/(float)Games.get(yearIter);
				float avgToDate = avg;
				avg = currentYearAvg*(1/(yearIter+1))+avgToDate*(1-1/(yearIter+1));
				System.out.println("Current Year Avg: "+currentYearAvg);
				System.out.println("Avg To Date:      "+avgToDate);
				System.out.println("New Avg:          "+avg);
			}
		}
		
		return avg;
	}
	
	private Pitcher			   CurrentPitcher;
	private final static float DOUBLE_POINTS = 2.0f;
	private Vector<Integer>    Games;
	private final static float HOME_RUN_POINTS = 4.0f;
	private final static float OUT_POINTS = -0.25f;
	private Map<String, Stats> PitcherStats;
	private final static float RBI_POINTS = 1.0f;
	private final static float RUN_POINTS = 1.0f;
	private final static float SINGLE_POINTS = 1.0f; 
	private final static float STOLEN_BASE_POINTS = 2.0f;
	private final static float TRIPLE_POINTS = 3.0f;
	private Vector<Stats>      YearStats;
	private final static float WALK_POINTS = 1.0f;
	
	public class Stats {
		public int AtBats;
		public int Doubles;
		public int Singles;
		public int Triples;
		public int HomeRuns;
		public int RBIs;
		public int Runs;
		public int StolenBases;
		public int Outs;
		public int Walks;
	}
	
	public static Stats constructStats() {
		Batter temp = new Batter();
		return temp.new Stats();
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
			} while(!innerHTML.equals("&nbsp;Batting"));
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
					currentStats.AtBats = Integer.parseInt(currentSeason.child(3).html());
					currentStats.Runs = Integer.parseInt(currentSeason.child(4).html());
					currentStats.Doubles = Integer.parseInt(currentSeason.child(6).html());
					currentStats.Triples = Integer.parseInt(currentSeason.child(7).html());
					currentStats.HomeRuns = Integer.parseInt(currentSeason.child(8).html());
					currentStats.RBIs = Integer.parseInt(currentSeason.child(9).html());
					currentStats.Walks = Integer.parseInt(currentSeason.child(10).html());
					currentStats.Outs = Integer.parseInt(currentSeason.child(11).html());
					currentStats.StolenBases = Integer.parseInt(currentSeason.child(12).html());
					currentStats.Singles = Integer.parseInt(currentSeason.child(5).html());
					currentStats.Singles -= currentStats.Doubles + currentStats.Triples + currentStats.HomeRuns;
					YearStats.set(seasonID, currentStats);
				} catch(IndexOutOfBoundsException ex) {break;}
			} while(seasonIter > 0);
			
		} catch (IOException e) {e.printStackTrace();}
	}
	
	@Override
	public String[] parse(String[] rawText) {
		return rawText;
	}
	
	//@Override
	/*public String[] parse(String[] rawText) {
		int season = 2011;

		// Throw away the header metadata
		while(!rawText[0].split(">")[0].equals("<season"))
			rawText = ArrayUtils.remove(rawText, 0);
		
		// Parse the season
		rawText[0] = rawText[0].replace("<season>", "");
		rawText[0] = rawText[0].replace("</season>", "");
		season -= Integer.parseInt(rawText[0]);
		rawText = ArrayUtils.remove(rawText, 0);
		
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
			if((type != null) && (!rawText[0].equals("-"))) statValue = Integer.parseInt(rawText[0]);
			rawText = ArrayUtils.remove(rawText, 0);
		
			if(type != null) {
				Stats stats = YearStats.get(season);
				switch(type) {
					case GAMES_PLAYED:
						Games.set(season, statValue);
						break;
					case AT_BATS:
						stats.AtBats = statValue;
						break;
					case RUNS:
						stats.Runs = statValue;
						break;
					case SINGLES:
						stats.Singles = statValue;
						break;
					case DOUBLES:
						stats.Doubles = statValue;
						break;
					case TRIPLES:
						stats.Triples = statValue;
						break;
					case HOME_RUNS:
						stats.HomeRuns = statValue;
						break;
					case RBIS:
						stats.RBIs = statValue;
						break;
					case STOLEN_BASES:
						stats.StolenBases = statValue;
						break;
					case WALKS:
						stats.Walks = statValue;
						break;
					case STRIKEOUTS:
						stats.Outs = statValue;
						break;
					default:
						System.err.println("Unknown Stat Category: "+rawStatType);
				}
				YearStats.set(season, stats);
			}
			
			// Remove the /stat tag
			rawText = ArrayUtils.remove(rawText, 0);
		}
		
		return rawText;
	}*/
	
	public enum StatsType {
		AT_BATS("At Bats"),
		GAMES_PLAYED("Games Played"),
		RUNS("Runs"),
		SINGLES("Singles"),
		DOUBLES("Doubles"),
		TRIPLES("Triples"),
		HOME_RUNS("Home Runs"),
		STOLEN_BASES("Stolen Bases"),
		WALKS("Walks"),
		STRIKEOUTS("Strike Outs"),
		RBIS("RBIs");
		
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
				case 6:
					return AT_BATS;
				case 7:
					return RUNS;
				case 9:
					return SINGLES;
				case 10:
					return DOUBLES;
				case 11:
					return TRIPLES;
				case 12:
					return HOME_RUNS;
				case 13:
					return RBIS;
				case 16:
					return STOLEN_BASES;
				case 18:
					return WALKS;
				case 21:
					return STRIKEOUTS;
				default:
					return null;
			}
		}
		
		String Value;
	}
}
