import org.apache.commons.lang3.ArrayUtils;


public class Player {
	public Player() {}
	
	public Player(final String name,
			      final PositionType position,
			      final float salary) {
		Name = name.toUpperCase();
		Position = position;
		Salary = salary;
	}
	
	public String[] parse(String[] rawText) {
		// Throw away the "<player>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Parse the player id
		rawText[0] = rawText[0].replace("<player_key>", "");
		rawText[0] = rawText[0].replace("</player_key>", "");
		ID = Integer.parseInt(rawText[0].split("\\.")[2]);
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "<player_key>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "<name>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Parse the player name
		rawText[0] = rawText[0].replace("<full>", "");
		rawText[0] = rawText[0].replace("</full>", "");
		Name = rawText[0].toUpperCase();
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "<first>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "<last>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "<ascii_first>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "<ascii_last>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the "</name>" tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the status tag
		if(rawText[0].split(">")[0].equals("<status"))
			rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the on disable list tag
		if(rawText[0].split(">")[0].equals("<on_disabled_list"))
			rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the <editorial_player_key> tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the <editorial_team_key> tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Parse the team
		rawText[0] = rawText[0].replace("<editorial_team_full_name>", "");
		rawText[0] = rawText[0].replace("</editorial_team_full_name>", "");
		Team = ValidTeams.parse(rawText[0].toUpperCase());
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the '<editorial_team_abbr>'
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the uniform number tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the position tag
		rawText[0] = rawText[0].replace("<display_position>", "");
		rawText[0] = rawText[0].replace("</display_position>", "");
		Position = PositionType.parse(rawText[0].split(",")[0]);
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the image tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the is droppable tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away position type tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the eligible position tags
		rawText = ArrayUtils.remove(rawText, 0);
		while(!rawText[0].split(">")[0].equals("</eligible_positions"))
			rawText = ArrayUtils.remove(rawText, 0);
		rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the has player notes tag
		if(rawText[0].split(">")[0].equals("<has_player_notes"))
			rawText = ArrayUtils.remove(rawText, 0);
		
		// Throw away the has recent player notes tag
		if(rawText[0].split(">")[0].equals("<has_recent_player_notes"))
			rawText = ArrayUtils.remove(rawText, 0);
			
		// Throw away the </player> tag
		rawText = ArrayUtils.remove(rawText, 0);
		
		return rawText;
	}
	
	public int getID() {
		return ID;
	}
	
	public String getName() {
		return Name;
	}
	
	public PositionType getPosition() {
		return Position;
	}
	
	public enum PositionType {
		PITCHER("Pitcher"),
		CATCHER("Catcher"),
		FIRST_BASE("First Base"),
		SECOND_BASE("Second Base"),
		THIRD_BASE("Third Base"),
		SHORT_STOP("Short Stop"),
		OUTFIELD("Outfield"),
		DESIGNATED_HITTER("Designated Hitter"),
		UTILITY("Utility");
		
		private PositionType(String value) {
			Value = value;
		}
		
		public static PositionType parse(String rawText) {
			if(rawText.equals("SP") || rawText.equals("P") || rawText.equals("RP"))
				return PITCHER;
			else if(rawText.equals("C"))
				return CATCHER;
			else if(rawText.equals("1B"))
				return FIRST_BASE;
			else if(rawText.equals("2B"))
				return SECOND_BASE;
			else if(rawText.equals("3B"))
				return THIRD_BASE;
			else if(rawText.equals("SS"))
				return SHORT_STOP;
			else if(rawText.equals("OF"))
				return OUTFIELD;
			else if(rawText.equals("Util"))
				return UTILITY;
			else {
				System.err.println("Unknown position: "+rawText);
				return null;
			}
		}
		
		String Value;
	}

	public enum ValidTeams {
		ARIZONA_DIAMONDBACKS("Arizona Diamondbacks"),
		ATLANTA_BRAVES("Atlanta Braves"),
		BALTIMORE_ORIOLES("Baltimore Orioles"),
		BOSTON_RED_SOX("Boston Red Sox"),
		CHICAGO_CUBS("Chicago Cubs"),
		CHICAGO_WHITE_SOX("Chicago White Sox"),
		CINCINNATI_REDS("Cincinnati Reds"),
		CLEVELAND_INDIANS("Cleveland Indians"),
		COLORADO_ROCKIES("Colorado Rockies"),
		DETROIT_TIGERS("Detroit Tigers"),
		HOUSTON_ASTROS("Houston Astros"),
		KANSAS_CITY_ROYALS("Kansas City Royals"),
		LOS_ANGELES_ANGELS("Los Angeles Angels"),
		LOS_ANGELES_DODGERS("Los Angeles Dodgers"),
		MIAMI_MARLINS("Miami Marlins"),
		MILWAUKEE_BREWERS("Milwaukee Brewers"),
		MINNESOTA_TWINS("Minnesota Twins"),
		NEW_YORK_METS("New York Mets"),
		NEW_YORK_YANKEES("New York Yankees"),
		OAKLAND_ATHLETICS("Oakland Athletics"),
		PHILADELPHIA_PHILLIES("Philadelphia Phillies"),
		PITTSBURGH_PIRATES("Pittsburgh Pirates"),
		SAN_DIEGO_PADRES("San Diego Padres"),
		SAN_FRANCISCO_GIANTS("San Francisco Giants"),
		SEATTLE_MARINERS("Seattle Mariners"),
		ST_LOUIS_CARDINALS("St. Louis Cardinals"),
		TAMPA_BAY_RAYS("Tampa Bay Rays"),
		TEXAS_RANGERS("Texas Rangers"),
		TORONTO_BLUE_JAYS("Toronto Blue Jays"),
		WASHINGTON_NATIONALS("Washington Nationals");
		
		ValidTeams(String value) {
			Value = value;
		}
		
		@Override
		public String toString() {
			return Value;
		}
		
		public static ValidTeams parse(String rawText) {
			if(rawText.equals("ARIZONA DIAMONDBACKS")) return ARIZONA_DIAMONDBACKS;
			else if(rawText.equals("ATLANTA BRAVES")) return ATLANTA_BRAVES;
			else if(rawText.equals("BALTIMORE ORIOLES")) return BALTIMORE_ORIOLES;
			else if(rawText.equals("BOSTON RED SOX")) return BOSTON_RED_SOX;
			else if(rawText.equals("CHICAGO CUBS")) return CHICAGO_CUBS;
			else if(rawText.equals("CHICAGO WHITE SOX")) return CHICAGO_WHITE_SOX;
			else if(rawText.equals("CINCINNATI REDS")) return CINCINNATI_REDS;
			else if(rawText.equals("CLEVELAND INDIANS")) return CLEVELAND_INDIANS;
			else if(rawText.equals("COLORADO ROCKIES")) return COLORADO_ROCKIES;
			else if(rawText.equals("DETROIT TIGERS")) return DETROIT_TIGERS;
			else if(rawText.equals("HOUSTON ASTROS")) return HOUSTON_ASTROS;
			else if(rawText.equals("KANSAS CITY ROYALS")) return KANSAS_CITY_ROYALS;
			else if(rawText.equals("LOS ANGELES ANGELS")) return LOS_ANGELES_ANGELS;
			else if(rawText.equals("LOS ANGELES DODGERS")) return LOS_ANGELES_DODGERS;
			else if(rawText.equals("MIAMI MARLINS")) return MIAMI_MARLINS;
			else if(rawText.equals("MILWAUKEE BREWERS")) return MILWAUKEE_BREWERS;
			else if(rawText.equals("MINNESOTA TWINS")) return MINNESOTA_TWINS;
			else if(rawText.equals("NEW YORK METS")) return NEW_YORK_METS;
			else if(rawText.equals("NEW YORK YANKEES")) return NEW_YORK_YANKEES;
			else if(rawText.equals("OAKLAND ATHLETICS")) return OAKLAND_ATHLETICS;
			else if(rawText.equals("PHILADELPHIA PHILLIES")) return PHILADELPHIA_PHILLIES;
			else if(rawText.equals("PITTSBURGH PIRATES")) return PITTSBURGH_PIRATES;
			else if(rawText.equals("SAN DIEGO PADRES")) return SAN_DIEGO_PADRES;
			else if(rawText.equals("SAN FRANCISCO GIANTS")) return SAN_FRANCISCO_GIANTS;
			else if(rawText.equals("SEATTLE MARINERS")) return SEATTLE_MARINERS;
			else if(rawText.equals("ST. LOUIS CARDINALS")) return ST_LOUIS_CARDINALS;
			else if(rawText.equals("TAMPA BAY RAYS")) return TAMPA_BAY_RAYS;
			else if(rawText.equals("TEXAS RANGERS")) return TEXAS_RANGERS;
			else if(rawText.equals("TORONTO BLUE JAYS")) return TORONTO_BLUE_JAYS;
			else if(rawText.equals("WASHINGTON NATIONALS")) return WASHINGTON_NATIONALS;
			else {
				System.err.println("Unknown team: "+rawText);
				return null;
			}
		}
		
		private String Value;
	}
	
	public float getValue() {
		return Value;
	}
	
	protected int		   ID;
	protected String	   Name;
	protected PositionType Position;
	protected float	       Salary;
	protected ValidTeams   Team;
	protected float 	   Value;
}
