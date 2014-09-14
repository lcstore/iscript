package com.lezo.iscript.yeam.message;

import com.lezo.iscript.common.BufferObjectWriter;
import com.lezo.iscript.service.crawler.dto.MessageDto;

public class MessageCacher {
    private static final int capacity = 500;
	private static final MessageCacher INSTANCE = new MessageCacher();
	private BufferObjectWriter<MessageDto> bufferWriter = new BufferObjectWriter<MessageDto>(new MessageWriter(), capacity);
	private MessageCacher(){
		
	}
	public static MessageCacher getInstance(){
		return INSTANCE;
	}
	
	public BufferObjectWriter<MessageDto> getBufferWriter() {
		return bufferWriter;
	}
}
