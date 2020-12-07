package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.github.pagehelper.Page;

@Mapper
public interface BoardMapper {
	
    boolean insertBoard(BoardVO b); // INSERT,UPDATE,DELETE 등 DB내용이 변경되는 경우, 해당 행 수를 return

    /*행을 추가하고 자동증가필드의 값을 parameter로 전달된 UserVO의 num변수에 저장*/
    int addAndGetKey(); // ﻿db저장(입력) 및 출력을 동시에 수행하는 userVO
   
    List<BoardVO> getBoardList();
    Page<BoardVO> BoardPaging();

    BoardVO getBoardById(int num);

    boolean updateBoard(BoardVO b);

    boolean deleteBoard(int num);
    
    int add(BoardVO b);	
    boolean attach_insert(AttachVO attach);
	
	BoardVO select(int num);
	List<AttachVO> fileList(int num);
	
	int countUpdate(int num);

	List<BoardVO> getIndexList();
    boolean get_index(int num);

	List<BoardVO> search(String sas, String sen);

	int replyInsert(BoardVO vo);

	BoardVO getBoardPnum();
}



