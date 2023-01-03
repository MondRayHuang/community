package com.nowcoder.community.util;

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
    //替换符
    private static final String REPLACEMENT = "***";

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
                //加载敏感词文件
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
                String word = "";
                while((word = bufferedReader.readLine()) != null){
                    this.addKeyword(word);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }

    }

    private void addKeyword(String word) {
        TrieNode trieNode = rootNode;
        for(int i = 0; i<word.length(); i++){
            char c = word.charAt(i);
            TrieNode subNode =  trieNode.getSubNode(c);

            //这个是新敏感词
            if(subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                //把子节点加上
                trieNode.setSubNodes(c, subNode);
            }
                //以子节点为起始点再往下加
                trieNode = subNode;

            //设置结束标志
            if(i == word.length()-1){
                trieNode.setKeywordEnd(true);
            }

        }
    }

    //定义前缀树
    private class TrieNode{
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点(key是下级字符，value是下级节点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //获取当前节点是否为最后字符
        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        //设定当前节点的状态
        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        //获取子节点
        public TrieNode getSubNode(Character character){
            return subNodes.get(character);
        }

        //设置子节点
        public void setSubNodes(Character character,TrieNode trieNode){
            subNodes.put(character,trieNode);
        }



    }

    /**
     * 过滤敏感词方法
     * @param text
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1,指向敏感树
        TrieNode trieNode = rootNode;
        //指针2，指向开始字符
        int begin = 0;
        //指针3，指向结束字符
        int position = 0;
        
        //装载结果
        StringBuffer result = new StringBuffer();

        while(begin < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                //若指针1处于根节点则直接跳到下一个节点
                if(trieNode == rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
            }

            //开始检测子节点
            trieNode = trieNode.getSubNode(c);
            if(trieNode == null){
                //从begin开始全部写入
                result.append(text.charAt(begin));
                //字符指针指向下一个字符
                position = ++begin;
                //重新指向根节点
                trieNode = rootNode;
            }else if(trieNode.isKeywordEnd()){
                //发现敏感词
                result.append(REPLACEMENT);
                begin = ++position;
                trieNode = rootNode;
            }else{
                position++;
            }
        }
        return result.toString();
    }

    //判断字符是否为符号
    private boolean isSymbol(char c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
