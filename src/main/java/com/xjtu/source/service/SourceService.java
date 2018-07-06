package com.xjtu.source.service;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.source.domain.Source;
import com.xjtu.source.repository.SourceRepository;
import com.xjtu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 处理source数据源
 *
 * @author yuanhao
 * @date 2018/3/4 17:07
 */
@Service
public class SourceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceRepository sourceRepository;

    /**
     * 插入数据源
     *
     * @param source 需要插入的数据源
     * @return 插入结果
     */
    public Result insertSource(Source source) {
        // 插入数据源不能为空
        String sourceName = source.getSourceName();
        if (sourceName == null || "".equals(sourceName) || sourceName.length() == 0) {
            logger.error("数据源插入失败：数据源名不存在或者为空");
            return ResultUtil.error(ResultEnum.SOURCE_INSERT_ERROR.getCode(), ResultEnum.SOURCE_INSERT_ERROR.getMsg());
        }
        // 插入数据源不存在
        if (sourceRepository.findBySourceIdAndSourceName(source.getSourceId(), source.getSourceName()) == null) {
            Source sourceInsert = sourceRepository.save(source);
            if (sourceInsert != null) {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), sourceInsert);
            } else {
                logger.error("数据源插入失败：数据库插入语句失败");
                return ResultUtil.error(ResultEnum.SOURCE_INSERT_ERROR_2.getCode(), ResultEnum.SOURCE_INSERT_ERROR_2.getMsg());
            }
        }
        // 插入领域已存在
        logger.error("数据源插入失败：插入已经存在的数据源");
        return ResultUtil.error(ResultEnum.SOURCE_INSERT_ERROR_1.getCode(), ResultEnum.SOURCE_INSERT_ERROR_1.getMsg());
    }

    /**
     * 删除数据源：根据数据源 Id
     *
     * @param sourceId 数据源id
     * @return 删除结果
     */
    public Result deleteSource(Long sourceId) {
        try {
            sourceRepository.delete(sourceId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除成功");
        } catch (Exception e) {
            logger.error("删除失败");
            return ResultUtil.error(ResultEnum.SOURCE_DELETE_ERROR.getCode(), ResultEnum.SOURCE_DELETE_ERROR.getMsg());
        }

    }

    /**
     * 更新数据源：根据数据源Id
     *
     * @param sourceId  数据源id
     * @param newSource 新增加的数据源信息
     * @return 更新结果
     */
    public Result updateSource(Long sourceId, Source newSource) {
        try {
            sourceRepository.updateBySourceId(sourceId, newSource.getSourceId(), newSource.getSourceName(), newSource.getNote());
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "更新成功");
        } catch (Exception e) {
            logger.error("更新失败");
            return ResultUtil.error(ResultEnum.SOURCE_UPDATE_ERROR.getCode(), ResultEnum.SOURCE_UPDATE_ERROR.getMsg());
        }
    }

    /**
     * 查询数据源：所有数据源
     *
     * @return 查询结果
     */
    public Result getSource() {
        List<Source> sources = sourceRepository.findAll();
        if (sources.size() > 0) {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), sources);
        } else {
            logger.error("数据源查询失败：没有数据源记录");
            return ResultUtil.error(ResultEnum.SOURCE_SEARCH_ERROR.getCode(), ResultEnum.SOURCE_SEARCH_ERROR.getMsg());
        }
    }

    /**
     * 查询数据源：根据数据源 Id
     *
     * @param sourceId 指定数据源 Id
     * @return 查询结果
     */
    public Result getSourceById(Long sourceId) {
        try {
            Source source = sourceRepository.findBySourceId(sourceId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), source);
        } catch (Exception e) {
            logger.error("数据源查询失败：没有数据源记录");
            return ResultUtil.error(ResultEnum.SOURCE_SEARCH_ERROR.getCode(), ResultEnum.SOURCE_SEARCH_ERROR.getMsg());
        }
    }

    /**
     * 获取分页数据源数据，按照数据源Id排序 (不带查询条件)
     *
     * @param page     第几页的数据
     * @param size     每页数据的大小
     * @param ascOrder 是否升序
     * @return 分页排序的数据
     */
    public Result getSourceByPagingAndSorting(Integer page, Integer size, boolean ascOrder) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        // 分页和排序条件，默认按照id排序
        Pageable pageable = new PageRequest(page, size, direction, "sourceId");
        Page<Source> sourcePage = sourceRepository.findAll(pageable);
        return sourcePageJudge(sourcePage);
    }

    /**
     * 获取分页数据源数据，按照数据源Id排序 (带查询条件：根据同一数据源Id下的数据)
     *
     * @param page     第几页的数据
     * @param size     每页数据的大小
     * @param ascOrder 是否升序
     * @param sourceId 数据源Id (查询条件)
     * @return 分页排序的数据
     */
    public Result getSourceByIdAndPagingAndSorting(Integer page, Integer size, boolean ascOrder, Long sourceId) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "sourceId");
        // lamada表达式的写法
        Page<Source> sourcePage = sourceRepository.findAll(
                (Root<Source> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
                    return criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
                }, pageable);
        return sourcePageJudge(sourcePage);
//        Page<Source> sourcePage = sourceRepository.findAll(new Specification<Source>(){
//            @Override
//            public Predicate toPredicate(Root<Source> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                // 多个条件查询
////                Predicate p1 = criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
////                Predicate p2 = criteriaBuilder.equal(root.get("sourceName").as(String.class), sourceName);
////                Predicate p3 = criteriaBuilder.equal(root.get("note").as(String.class), note);
////                query.where(criteriaBuilder.and(p1,p2,p3));
////                return query.getRestriction();
//                return criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
//            }
//        }, pageable);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     *
     * @param sourcePage 分页查询结果
     * @return 返回查询结果
     */
    public Result sourcePageJudge(Page<Source> sourcePage) {
        if (sourcePage.getTotalElements() == 0) {
            logger.error("数据源分页查询失败：没有数据源记录");
            return ResultUtil.error(ResultEnum.SOURCE_SEARCH_ERROR_1.getCode(), ResultEnum.SOURCE_SEARCH_ERROR_1.getMsg());
        } else if (sourcePage.getNumber() >= sourcePage.getTotalPages()) {
            logger.error("数据源分页查询失败：查询的页数超过最大页数");
            return ResultUtil.error(ResultEnum.SOURCE_SEARCH_ERROR_2.getCode(), ResultEnum.SOURCE_SEARCH_ERROR_2.getMsg());
        }
        // 返回查询的内容
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), sourcePage);
    }

}
