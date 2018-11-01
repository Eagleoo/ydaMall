package com.way.note.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author way
 * 
 */
public class NoteList {

	private List<NoteItem> mNoteLists = new LinkedList<NoteItem>();

	private boolean dataChanged = true;

	private void sort() {
		Collections.sort(mNoteLists, NoteComparetor.comparetor);
	}

	public List<NoteItem> getList() {
		return mNoteLists;
	}

	public List<NoteItem> getNotes() {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (!noteItem.isFileFolder) {
				arrayList.add(noteItem);
			}
		}
		return arrayList;
	}

	public List<NoteItem> getFolderList() {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (noteItem.isFileFolder) {
				arrayList.add(noteItem);
			}
		}
		return arrayList;
	}

	public List<NoteItem> getNotesFromFolder(int folderID) {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (noteItem.parentFolder == folderID) {
				arrayList.add(noteItem);
			}
		}
		return arrayList;
	}

	public List<NoteItem> getRootFoldersAndNotes() {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (noteItem.isFileFolder || noteItem.parentFolder == -1) {
				arrayList.add(noteItem);
			}
		}
		return arrayList;
	}

	public List<NoteItem> getRootNotes() {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (!noteItem.isFileFolder && noteItem.parentFolder == -1) {
				arrayList.add(noteItem);
			}
		}
		return arrayList;
	}

	public NoteItem getNoteItemByID(int ID) {
		for (NoteItem noteItem : mNoteLists) {
			if (noteItem._id == ID) {
				return noteItem;
			}
		}
		return null;
	}

	public List<NoteItem> getColckAlarmItems() {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (noteItem.alarmEnable) {
				arrayList.add(noteItem);
			}
		}
		return arrayList;
	}

	public List<NoteItem> getNotesIncludeContent(String content) {
		ArrayList<NoteItem> arrayList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (!noteItem.isFileFolder && noteItem.content != null) { // 如果是便签并且不为空
				if (noteItem.content.toLowerCase().contains(
						content.toLowerCase())) { // include content
					arrayList.add(noteItem);
				} else if (noteItem.title != null
						&& noteItem.title.toLowerCase().contains(
								content.toLowerCase())) {
					arrayList.add(noteItem);
				}
			}
		}
		return arrayList;
	}

	public void updateOneItem(NoteItem item) {
		mNoteLists.remove(item);
		mNoteLists.add(item);
		sort();
	}

	public void addOneItem(NoteItem item) {
		mNoteLists.add(item);
		sort();
	}

	public void deleteNoteItemOrFolder(NoteItem item) {
		List<NoteItem> removeList = new ArrayList<NoteItem>();
		for (NoteItem noteItem : mNoteLists) {
			if (noteItem.parentFolder == item._id || noteItem._id == item._id) {
				removeList.add(noteItem);
			}
		}
		mNoteLists.removeAll(removeList);
	}

	public void clear() {
		mNoteLists.clear();
	}

	public void add(NoteItem item) {
		mNoteLists.add(item);
		sort();
	}

	public static class NoteComparetor implements Comparator<NoteItem> {

		public static final NoteComparetor comparetor = new NoteComparetor();

		@Override
		public int compare(NoteItem item1, NoteItem item2) {
			if (item1.isFileFolder != item2.isFileFolder) {
				if (item1.isFileFolder) {
					return -1;
				} else {
					return 1;
				}
			} else {
				if (item1.date > item2.date) {
					return -1;
				} else if (item1.date < item2.date) {
					return 1;
				} else {
					if (item1._id > item2._id) {
						return -1;
					} else if (item1._id < item2._id) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		}

	}

	@Override
	public String toString() {
		return mNoteLists.toString();
	}
}
