package com.xjtu.spider_topic.controller;


/**
 * 知识主题爬虫；知识主题抽取
 *
 * @author 张铎
 * @date 2019年7月
 */
@RestController
@RequestMapping("/spider_topic")
public class TSpideController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SpiderService spiderService;

    @ApiOperation(value = "webmagic爬取课程碎片", notes = "webmagic爬取课程碎片")
    @GetMapping("/crawlAssembles")
    public ResponseEntity crawlAssembles() {
        Result result = spiderService.crawlAssembles();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}