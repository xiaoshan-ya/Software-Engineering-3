package nju.sentistrength.project.service.impl;

import nju.sentistrength.project.dao.DataMapper;
import nju.sentistrength.project.model.Data;
import nju.sentistrength.project.service.DataService;
import nju.sentistrength.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by auto on 2023/05/19.
 */
@Service
@Transactional
public class DataServiceImpl extends AbstractService<Data> implements DataService {
    @Resource
    private DataMapper dataMapper;

}
