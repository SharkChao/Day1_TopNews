package com.lenovohit.administrator.treelistview.tree_bean;

import com.lenovohit.administrator.treelistview.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by SharkChao on 2017-08-16.
 */

public class NodeHelper<T>{
    List<T>beanData = new ArrayList<>();
    List<Node>nodeList = new ArrayList<>();

    public NodeHelper(List<T>beanData) throws IllegalAccessException {
        this.beanData = beanData;
        List<Node> list = setBean2Note();
        List<Node> rootNode = getRootNode(list);
        for (Node node:rootNode){
            sortNode(nodeList,node,1);
        }
    }
    public List<Node> setBean2Note() throws IllegalAccessException {
        List<Node>list = new ArrayList<>();
        list.clear();
            for (int i=0; i<beanData.size(); i++){
                T t = beanData.get(i);
                Class<?> aClass = t.getClass();
                Field[] declaredFields = aClass.getDeclaredFields();
                String id = "-1";
                String pid = "-1";
                String title = "";
                for (Field field:declaredFields){
                    if (field.getAnnotation(TreeNodeId.class) != null){
                        field.setAccessible(true);
                         id = (String) field.get(t);
                    }
                    if (field.getAnnotation(TreeNodePid.class) != null){
                        field.setAccessible(true);
                         pid = (String) field.get(t);
                    }
                    if (field.getAnnotation(TreeNodeTitle.class) != null){
                        field.setAccessible(true);
                         title = (String) field.get(t);
                    }
                }
                Node node = new Node(id,pid,title);
                list.add(node);
            }
            for (int i = 0;i<list.size();i++){
                Node m = list.get(i);
                for (int j=i+1;j<list.size();j++){
                    Node n = list.get(j);
                    if (m.getId().equals(n.getPid())){
                        m.getChildNode().add(n);
                        n.setParentNode(m);
                    }else if (m.getPid().equals(n.getId())){
                        n.getChildNode().add(m);
                        m.setParentNode(n);
                    }
                }
            }
            for (Node node:list){
                setNodeIcon(node);
            }
            return list;
    }

    /**
     * 获取根节点
     * @param nodeList
     * @return
     */
    public List<Node> getRootNode(List<Node>nodeList){
        List<Node>rootNode = new ArrayList<>();
        rootNode.clear();
        for (int i = 0; i<nodeList.size(); i++){
            Node node = nodeList.get(i);
            if (node.getPid().equals("0")){
                //没有父节点
                rootNode.add(node);
            }else {
            }
        }
        return rootNode;
    }

    /**
     * 对节点进行排序
     */
    public void sortNode(List<Node>list,Node node,int lenvel){
        //先将根节点添加进来
        node.setLevel(lenvel+"");
        list.add(node);
        //判断是不是叶子节点
        if (node.getChildNode() == null || node.getChildNode().size()<=0){
            return ;
        }
        List<Node> childNode = node.getChildNode();
        Collections.sort(childNode);
        for (Node child : childNode){
            sortNode(list,child,lenvel+1);
        }
    }

    public List<Node> getList() {
        return nodeList;
    }

    private  void setList(List<Node> list) {
        this.nodeList = list;
    }
    public List<Node> getVisableNode(List<Node> list){
        List<Node> visableList = new ArrayList<>();
        for (Node node : list){
            if (node.getParentNode() == null || node.getParentNode().isExpand()){
                setNodeIcon(node);
                visableList.add(node);
            }
        }
        return visableList;
    }
    /**
     * 设置节点的图标
     *
     * @param node
     */
    private static void setNodeIcon(Node node)
    {
        if (node.getChildNode().size() > 0 && node.isExpand())
        {
            node.setResId(R.mipmap.tree_ex);
        } else if (node.getChildNode().size() > 0 && !node.isExpand())
        {
            node.setResId(R.mipmap.tree_ec);
        } else
            node.setResId(-1);

    }
}
