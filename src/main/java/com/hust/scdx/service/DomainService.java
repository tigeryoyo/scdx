package com.hust.scdx.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.hust.scdx.model.Domain;
import com.hust.scdx.model.DomainOne;
import com.hust.scdx.model.DomainTwo;
import com.hust.scdx.model.params.DomainOneQueryCondition;
import com.hust.scdx.model.params.DomainTwoQueryCondition;

public interface DomainService {

	/**
	 * 根据指定条件查询一级域名,为null则查询所有
	 * @param condition 如需分页，请在condition中设置start,limit
	 * @return 
	 */
	List<DomainOne> getDomainOneByCondition(DomainOneQueryCondition condition);

	/**
	 * 根据条件查询二级域名，null则查询所有
	 * @param condition
	 * @return
	 */
	List<DomainTwo> getDomainTwoByCondition(DomainTwoQueryCondition condition);
	
	/**
	 * 根据一级域名查询对应的二级域名
	 * @return 返回的二级域名顺序与一级域名一一对应 List< one --> List<two>>
	 */
	List<List<DomainTwo>> getDomainTwoByOne(List<DomainOne> list);

	/**
	 * 从原始文件或标准数据中读取url，将其中的未知url包含基本属性添加到数据库，url存在则不予处理，不存在则插入
	 * @param file
	 * @return 添加成功返回true，添加失败或文件读取错误返回false
	 */
	boolean addUnknowUrlFromFile(MultipartFile file);

	/**
	 * 从导入的映射表中读取url基本信息，并做分级处理后添加到数据库中，已有的url信息做更新操作
	 * @param file
	 * @return 若文件格式不正确读取错误或添加失败返回false
	 */
	boolean addUrlFromFile(MultipartFile file);

	/**
	 * 添加未知的域名信息（基本用作处理从基础数据中提取到的域名信息）
	 * 先清洗出完整域名，在做域名分级处理
	 * 若存在则不做处理返回false
	 * @param domian 未知来源的域名信息，必须包含url属性
	 * @return 对数据库影响行数大于0返回true
	 */
	boolean addUnknowDomain(Domain domain);

	/**
	 * 添加未知的域名信息（基本用作处理从基础数据中提取到的域名信息）
	 * 从上往下的处理流程
	 * 先清洗出完整域名，在做域名分级处理
	 * @param domianList 未知来源的域名信息，必须包含url属性，其他属性可以不设置，若存在也可设置
	 * @return 对数据库影响行数大于0返回true
	 */
	boolean addUnknowDomain(List<Domain> domainList);

	/**
	 * 处理来源于人工修改后的excel文件的域名信息，只做更新操作
	 * 从下往上的处理流程
	 * 以excel数据为基准 更新已经存在的域名信息
	 * @param domain 人工处理后的域名信息，必须包含url属性，同时其他属性基本健全
	 * @return 对数据库影响行数大于0返回true
	 */
	boolean addDomain(Domain domain);

	/**
	 * 根据uuid删除一级域名
	 * @param uuid
	 * @return 删除成功返回true，失败返回false
	 */
	boolean deleteDomainOneById(String uuid);

	/**
	 * 获取所有一级域名数
	 * @return
	 */
	long getDomainOneCount();

	/**
	 * 根据id删除二级域名
	 * @param uuid
	 * @return 删除成功返回true，失败返回false
	 */
	boolean deleteDomainTwoById(String uuid);

	/**
	 * 根据id获取一级域名信息
	 * @param uuid
	 * @return
	 */
	DomainOne getDomainOneById(String uuid);

	/**
	 * 根据id获取二级域名信息
	 * @param uuid
	 * @return
	 */
	DomainTwo getDomainTwoById(String uuid);

	/**
	 * 根据给定一级域名信息更新数据库，必须包含uuid	
	 * @param one
	 * @return 更新成功返回true，失败返回false
	 */
	boolean updateDomainOne(DomainOne one);
	
	/**
	 * 根据给定二级域名信息更新数据库，必须包含uuid	
	 * @param two
	 * @return 更新成功返回true，失败返回false
	 */
	boolean updateDomainTwo(DomainTwo two);
	
	/**
	 * 根据给定的url获取到域名信息
	 * @param url 完整的url
	 * @return 清洗过后的域名对象
	 */
	Domain getDomainByUrl(String url);
}
