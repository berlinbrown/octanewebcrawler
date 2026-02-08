package org.berlin.logs.scan.terms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Term handler.
 */
public class LineTermInfoHandler {

	enum TermType {
		none, begin, end, error, exception, nullptr, criticalerror, searchterm
	};

	private final LogFileStatistics stats;

	int processIndex = 0;

	TermType termType = TermType.none;

	ServerInfo serverInfo = new ServerInfo();

	String fileDate = "";
	String year = "";
	String month = "";
	String day = "";
	String hour = "";
	String minute = "";
	String seconds = "";
	String timeField = "";
	String dayField = "";
	String dateTimeField = "";
	int imonth = 0;
	int iday = 0;
	int ihour = 0;
	int iminute = 0;

	long timeByDay = 1;
	long timeByHour = 1;
	long timeByMin = 1;
	long timeByTenMin = 1;

	Date javaDate = null;
	boolean todayOrYesterday = false;
	boolean errorAtWeek = false;

	public static final String FORMAT_DATE_TIME = "yyyy.MM.dd HH:mm:ss";

	private static final Pattern patternBegin = Pattern.compile(".* (onBeginRequest) .*");
	private static final Pattern patternEnd = Pattern.compile(".* (onEndRequest) .*");
	private static final Pattern patternCritical = Pattern.compile(".*(ERROR).*");
	private static final Pattern patternException = Pattern.compile(".*(Exception).*");
	private static final Pattern patternError = Pattern.compile(".* (?i)(ERROR) .*");
	private static final Pattern patternNullPointer = Pattern.compile(".*(NullPointerException).*");

	private static final String REPL_DOT = "\\\\.";
	private static final String REPL_LEFTP = "\\\\(";
	private static final String REPL_RIGHTP = "\\\\)";

	public static final String FORMAT_DAY = "x%s%s%s";
	public static final String FORMAT_TIME = "%s.%s.%s %s:%s:%s";

	/**
	 * Constructor.
	 * 
	 * @param stats
	 */
	public LineTermInfoHandler(final LogFileStatistics stats) {
		this.stats = stats;
	}

	/**
	 * Scan for search term.
	 * 
	 * @param line
	 * @param searchTermRegex
	 * @param onlyReturnValidTime
	 * @return
	 */
	public boolean processSearchTerm(final String line, final String searchTermRegex,
			final boolean onlyReturnValidTime) {
		if (line == null) {
			return false;
		}
		if (searchTermRegex == null || searchTermRegex.length() == 0) {
			return false;
		}
		// Avoid regex characters, escape
		String term = searchTermRegex.replaceAll("\\.", REPL_DOT);
		term = ".*(?i)(" + term + ").*";
		final Pattern pattern = Pattern.compile(term);
		final Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			this.termType = TermType.searchterm;
			if (!onlyReturnValidTime) {
				return true;
			}
			return this.processTime(line);
		} // End of the if //
		return false;
	}

	/**
	 * Process the line. Retain relevant data.
	 * 
	 * @param line
	 */
	public boolean processAtSampling(final String line) {
		if (line == null) {
			return false;
		}
		final Matcher matcherBegin = this.patternBegin.matcher(line);
		final Matcher matcherEnd = this.patternEnd.matcher(line);
		final Matcher matcherCritical = this.patternCritical.matcher(line);
		final Matcher matcherException = this.patternException.matcher(line);
		final Matcher matcherError = this.patternError.matcher(line);
		final Matcher matcherNull = this.patternError.matcher(line);

		if (matcherBegin.matches()) {
			this.termType = TermType.begin;

		} else if (matcherEnd.matches()) {
			this.termType = TermType.end;

		} else if (matcherCritical.matches()) {
			this.termType = TermType.criticalerror;

		} else if (matcherNull.matches()) {
			this.termType = TermType.nullptr;

		} else if (matcherException.matches()) {
			this.termType = TermType.exception;

		} else if (matcherError.matches()) {
			this.termType = TermType.error;
		} // End of the if - else //

		return this.processTime(line);
	}

	public boolean processTime(final String line) {
		if (line == null) {
			return false;
		}
		if (line.length() < 30) {
			return false;
		}
		this.year = line.substring(0, 4);
		this.month = line.substring(5, 7);
		this.day = line.substring(8, 10);
		this.hour = line.substring(11, 13);
		this.minute = line.substring(14, 16);
		try {
			this.imonth = Integer.parseInt(this.month);
			this.iday = Integer.parseInt(this.day);
			this.ihour = Integer.parseInt(this.hour);
			this.iminute = Integer.parseInt(this.minute);

			this.imonth = this.imonth < 1 ? 1 : this.imonth;
			this.iday = this.iday < 1 ? 1 : this.iday;
			this.ihour = this.ihour < 1 ? 1 : this.ihour;
			this.iminute = this.iminute < 1 ? 1 : this.iminute;

			// Note: time by day, 32 is the max day, arbitrary value.
			this.timeByDay = this.iday + (32 * this.imonth);
			this.timeByHour = this.ihour + (24 * this.timeByDay);
			this.timeByTenMin = (this.iminute / 10) + (6 * this.timeByHour);
			this.timeByMin = this.iminute + (60 * this.timeByHour);
		} catch (final NumberFormatException nfe) {
			return false;
		}
		this.seconds = line.substring(17, 19);
		this.dayField = String.format(FORMAT_DAY, this.month, this.day, this.year);
		final String tmpj = String.format(FORMAT_TIME, this.year, this.month, this.day, this.hour, this.minute,
				this.seconds);
		this.dateTimeField = tmpj;
		try {
			final SimpleDateFormat f = new SimpleDateFormat(FORMAT_DATE_TIME);
			final Date d = f.parse(tmpj);
			javaDate = d;
		} catch (final Exception e) {
			System.err.println("ERROR: line=" + line + " attemptYear=[" + this.year + "]");
			e.printStackTrace();
		}
		return this.validtime();
	}

	/**
	 * Return true if we need to persist this data.
	 * 
	 * @param line
	 * @return
	 */
	public boolean processAtAllLines(final String line) {

		final Matcher matcherBegin = this.patternBegin.matcher(line);
		final Matcher matcherEnd = this.patternEnd.matcher(line);
		final Matcher matcherCritical = this.patternCritical.matcher(line);
		final Matcher matcherException = this.patternException.matcher(line);
		final Matcher matcherError = this.patternError.matcher(line);
		final Matcher matcherNull = this.patternNullPointer.matcher(line);

		if (matcherBegin.matches()) {
			this.processTime(line);
			this.termType = TermType.begin;
			if (this.stats != null) {
				this.stats.totalBeginRequest++;
			}

		} else if (matcherEnd.matches()) {
			this.processTime(line);
			this.termType = TermType.end;
			if (this.stats != null) {
				this.stats.totalEndRequest++;
			}

		} else if (matcherCritical.matches()) {
			this.processTime(line);
			this.termType = TermType.criticalerror;
			if (this.stats != null) {
				this.stats.totalCriticalError++;
			}
			return validtime();

		} else if (matcherNull.matches()) {
			this.processTime(line);
			this.termType = TermType.nullptr;
			if (this.stats != null) {
				this.stats.totalNullPointer++;
			}
			return validtime();

		} else if (matcherException.matches()) {
			this.processTime(line);
			this.termType = TermType.exception;
			if (this.stats != null) {
				this.stats.totalException++;
			}
			return validtime();

		} else if (matcherError.matches()) {
			this.processTime(line);
			this.termType = TermType.error;
			if (this.stats != null) {
				this.stats.totalError++;
			}
		}
		return false;
	}

	public boolean processAtAllLines(final String line, final String searchTermRegex,
			final boolean onlyReturnValidTime) {
		// Search term has precedence
		final boolean hasSearchTerm = this.processSearchTerm(line, searchTermRegex, onlyReturnValidTime);
		if (hasSearchTerm) {
			if (this.stats != null) {
				this.stats.totalSearchTermFound++;
			}
			return true;
		} else {
			return this.processAtAllLines(line);
		} // End of the if - else //
	}

	protected final boolean validtime() {
		if (this.dateTimeField == null || this.dateTimeField.length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the termType
	 */
	public TermType getTermType() {
		return termType;
	}

} // End of Class //
