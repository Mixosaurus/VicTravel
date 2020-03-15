package cn.vic.travel.conversation;

import android.content.Context;

import java.io.Serializable;

import cn.bmob.newim.bean.BmobIMConversationType;

/**
 * Snake 创建于 2018/7/29 21:47
 * 会话基类
 */
public abstract class Conversation implements Serializable, Comparable {

        /**
         * 会话id
         */
        protected String conversationId;
        /**
         * 会话类型
         */
        protected BmobIMConversationType conversationType;

        /**
         * 会话名称
         */
        protected String conversationName;

        /**
         * 获取头像-用于会话界面显示
         */
        abstract public Object getAvatar();

        /**
         * 获取最后一条消息的时间
         */
        abstract public long getLastMessageTime();

        /**
         * 获取最后一条消息的时间
         *
         * @return
         */
        abstract public String getLastMessageContent();

        /**
         * 获取未读会话个数
         *
         * @return
         */
        abstract public int getUnReadCount();

        /**
         * 将所有消息标记为已读
         */
        abstract public void readAllMessages();

        /**
         * 点击事件
         *
         * @param context
         */
        abstract public void onClick(Context context);

        /**
         * 长按事件
         *
         * @param context
         */
        abstract public void onLongClick(Context context);


        public String getConversationName() {
            return conversationName;
        }

        public String getConversationId() {
            return conversationId;
        }

        public BmobIMConversationType getConversationType() {
            return conversationType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Conversation that = (Conversation) o;
            if (!conversationId.equals(that.conversationId)) return false;
            return conversationType == that.conversationType;
        }

        @Override
        public int hashCode() {
            int result = conversationId.hashCode();
            result = 31 * result + conversationType.hashCode();
            return result;
        }


        @Override
        public int compareTo(Object another) {
            if (another instanceof Conversation) {
                Conversation anotherConversation = (Conversation) another;
                long timeGap = anotherConversation.getLastMessageTime() - getLastMessageTime();
                if (timeGap > 0) return 1;
                else if (timeGap < 0) return -1;
                return 0;
            } else {
                throw new ClassCastException();
            }
        }

}

