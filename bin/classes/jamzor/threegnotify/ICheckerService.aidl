package jamzor.threegnotify;

interface ICheckerService {
	void enable();
	void disable();
	void vibrate(String pref);
}