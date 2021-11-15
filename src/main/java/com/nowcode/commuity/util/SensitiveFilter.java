package com.nowcode.commuity.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init(){
        try (
                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

        ) {
            String keyWord;
            while ((keyWord= buffer.readLine() )!= null){
                this.addKeyword(keyWord);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败"+e.getMessage());
        }

    }

    //构造树
    private void addKeyword(String keyWord) {
        TrieNode tmpNode = root;
        for(int i =0;i < keyWord.length();i++){
            Character c = keyWord.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tmpNode.addSubNode(c,subNode);
            }

            tmpNode = subNode;
            if(i == keyWord.length()-1){
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    /**
     *过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tmpNode = root;
        int begin = 0;
        int position = 0;

        StringBuilder res = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){

                if(tmpNode == root){
                    res.append(c);
                    begin++;
                }

                position++;
                continue;
            }
            tmpNode = tmpNode.getSubNode(c);
            if(tmpNode == null){
                res.append(text.charAt(begin));
                position = ++begin;
                tmpNode = root;
            }else if (tmpNode.isKeywordEnd()){
                res.append(REPLACEMENT);
                begin = ++position;
                tmpNode = root;
            }else if(position+1 == text.length()){
                res.append(text.charAt(begin));
                position = ++begin;
                tmpNode = root;
            }else {
                position++;
            }
        }
        return begin <text.length() ? res.append(text.substring(begin)).toString() : res.toString();

    }

    //判断是否为特殊符号
    private boolean isSymbol(Character c){
        //0x2E80~ 0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树结构
    private class TrieNode{

        //关键词结束的标志
        private boolean isKeywordEnd = false;

        //key是下级字符，value是下级节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();


        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
