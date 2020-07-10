/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.util.xml;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XmlMapperTest {

	private String xml = "<xml><ToUserName><![CDATA[gh_c5b0d48c6751]]></ToUserName>\n"
			+ "<FromUserName><![CDATA[oomRkuFJSBvtioUODgukB8Q4fMWE]]></FromUserName>\n"
			+ "<CreateTime>1589984327</CreateTime>\n" + "<MsgType><![CDATA[text]]></MsgType>\n"
			+ "<Content><![CDATA[哦哦哦]]></Content>\n" + "<MsgId>22763099515496730</MsgId>\n" + "</xml>";

	String xml1 = "<xml>\n" + " <ToUserName>wwddddccc7775555aaa</ToUserName>  \n"
			+ "  <FromUserName>sys</FromUserName>  \n" + "  <CreateTime>1527838022</CreateTime>  \n"
			+ "  <MsgType>event</MsgType>  \n" + "  <Event>open_approval_change</Event>\n" + "  <AgentID>1</AgentID>\n"
			+ "  <ApprovalInfo> \n" + "    <ThirdNo>201806010001</ThirdNo>  \n" + "    <OpenSpName>付款</OpenSpName>  \n"
			+ "    <OpenTemplateId>1234567890</OpenTemplateId> \n" + "    <OpenSpStatus>1</OpenSpStatus>  \n"
			+ "    <ApplyTime>1527837645</ApplyTime>  \n" + "    <ApplyUserName>xiaoming</ApplyUserName>  \n"
			+ "    <ApplyUserId>1</ApplyUserId>  \n" + "    <ApplyUserParty>产品部</ApplyUserParty>  \n"
			+ "    <ApplyUserImage>http://www.qq.com/xxx.png</ApplyUserImage>  \n" + "    <ApprovalNodes> \n"
			+ "      <ApprovalNode> \n" + "        <NodeStatus>1</NodeStatus>  \n" + "        <NodeAttr>1</NodeAttr> \n"
			+ "        <NodeType>1</NodeType>  \n" + "        <Items> \n" + "          <Item> \n"
			+ "            <ItemName>xiaohong</ItemName>  \n" + "            <ItemUserId>2</ItemUserId> \n"
			+ "            <ItemImage>http://www.qq.com/xxx.png</ItemImage>  \n"
			+ "            <ItemStatus>1</ItemStatus>  \n" + "            <ItemSpeech></ItemSpeech>  \n"
			+ "            <ItemOpTime>0</ItemOpTime> \n" + "          </Item> \n" + "        </Items> \n"
			+ "      </ApprovalNode> \n" + "    </ApprovalNodes>  \n" + "    <NotifyNodes> \n" + "      <NotifyNode> \n"
			+ "        <ItemName>xiaogang</ItemName>  \n" + "        <ItemUserId>3</ItemUserId> \n"
			+ "        <ItemImage>http://www.qq.com/xxx.png</ItemImage>  \n" + "      </NotifyNode> \n"
			+ "    </NotifyNodes> \n" + "    <approverstep>0</approverstep>  \n" + "  </ApprovalInfo> \n" + "</xml>\n";

	@Test
	void toMap() throws XMLStreamException {
		Map<String, String> context = new XmlMapper().toMap(xml1);
		System.out.println(context);
	}

}