package com.way.note.data;

import java.util.List;

import android.content.Context;

/**
 * 便签管理
 * 
 * @author way
 * 
 */
public interface NoteDataManager {

	public void initData(Context context);// 初始化数据

	public List<NoteItem> getFolderAndAllItems();// 获取所有文件夹和所有便签

	public NoteItem getNoteItem(int id);// 通过id获取通过id

	public NoteItem getNoteItemFromDB(int id);

	public int insertItem(NoteItem item);

	public int updateItem(NoteItem item);

	public List<NoteItem> getFolders();

	/**
	 * get notes, not include note forld.
	 */
	public List<NoteItem> getNotes();

	public List<NoteItem> getRootFoldersAndNotes();

	public List<NoteItem> getRootNotes();

	public List<NoteItem> getNotesFromFolder(int folderID);

	public void deleteNoteItem(NoteItem item);

	public void deleteAllNotes();

	public List<NoteItem> getColckAlarmItems();

	public List<NoteItem> getNotesIncludeContent(String content);

	public List<NoteItem> getColckAlarmItemsFromDB();
}
