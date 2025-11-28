package splat.executor;
// wrapper for int runtime vals
public class IntegerValue extends Value {

	private int value;
	
	public IntegerValue(int value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}