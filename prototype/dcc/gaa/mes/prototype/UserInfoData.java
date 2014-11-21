package dcc.gaa.mes.prototype;

import java.util.Set;

public class UserInfoData {
	public String userName;
	public int index;
	public Set<String> fileNames;
	public Double focus;
	public Double spread;

	public UserInfoData(String userName, int index, Set<String> fileNames) {
		super();
		this.userName = userName;
		this.index = index;
		this.fileNames = fileNames;
	}

	public String getUserName() {
		return userName;
	}

	public int getIndex() {
		return index;
	}

	public Set<String> getFileNames() {
		return fileNames;
	}

	public Double getFocus() {
		return focus;
	}

	public void setFocus(Double focus) {
		this.focus = focus;
	}

	public Double getSpread() {
		return spread;
	}

	public void setSpread(Double spread) {
		this.spread = spread;
	}

	public int getnFiles() {
		return fileNames.size();
	}

}