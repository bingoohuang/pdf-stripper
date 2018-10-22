# pdf-stripper

[![Build Status](https://travis-ci.org/bingoohuang/pdf-stripper.svg?branch=master)](https://travis-ci.org/bingoohuang/pdf-stripper)
[![Coverage Status](https://coveralls.io/repos/github/bingoohuang/pdf-stripper/badge.svg?branch=master)](https://coveralls.io/github/bingoohuang/pdf-stripper?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/pdf-stripper/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/pdf-stripper/)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


strip structured info from pdf

## PDF 信息提取
### 文本提取

```java
@Cleanup val is = new FileInputStream("演示.pdf");
val text = PdfStripper.stripText(is, PdfPagesSelect.allPages());

```

### 文本关键值获取

#### 从整个文本中搜索提取指定标签锚定的值（整行模式），例如：
<pre>
 测试者： 张晓平                                                                              
 企业用户： 智联测评                                                                               
 测评项目： 样本报告                                                                               
 测试日期： 2016-06-12                                                                         
 测试有效性： 比较可信    
</pre>

```java
val textMatcher = new TextMatcher(text);
assertThat(textMatcher.findLineLabelText("测试者：")).isEqualTo("张晓平");
assertThat(textMatcher.findLineLabelText("企业用户：")).isEqualTo("智联测评");
assertThat(textMatcher.findLineLabelText("测试日期：")).isEqualTo("2016-06-12");

```

#### 从整个文本搜索指定标签锚定的值，例如：
<pre>
2.1  情绪管理能力                                                                                                                                                
维度含义：                                                                                                        
   个体对自身和他人情绪进行有效认知或感知、激励和调控，充分挖掘和培养驾驭情绪                                                                     
的能力，包括自我情绪管理能力和他人情绪管理能力。                                                                                     
                                                                                                          
               很低                 较低            中等             较高             很高                             
                                                                                       5.5  
     总体而言，张晓平的情绪管理能力在人群中处于中等水平，说明其情绪管理能力尚可。                
</pre>

取到5.5，我们以`很高`作为标签锚点，去搜索5.5这个值。

```java
val textMatcher = new TextMatcher(text);
val t1 = textMatcher.findLabelText("很高", new TextMatcherOption("2.1  情绪管理能力", "的情绪管理能力在人群中"));
assertThat(t1).isEqualTo("5.5");
```

#### 从整个文本中搜索指定正则模式，提取所需信息，例如：
<pre>
   安全稳定型方面的职业价值观特点                                                                                                    
                                                   　薪酬福利：薪酬福利与我的能力和付出相匹配，令我满意。                                                        
   薪酬福利       　                     8.3            　价值特征：关注工作支付给自己的薪资待遇，喜欢有较高经济回报的工作环境和岗位。                                            
                                                                                                                                       
   工作稳定       　               6.1                  　工作稳定：我能和工作单位达成稳定的雇佣关系。                                                            
                                                   　价值特征：较为关注工作的稳定性和持久性，不会太乐意接受变数或挑战性较大的工作。                                                                                                                                         
   公平公正       　         3.5                        　公平公正：我能够在单位中获得公平公正的对待。                                                            
                                                   　价值特征：不太关注自己所在的组织是否具备完善的制度和公正的工作氛围，且在体制不太完善的组织中                                    
                                                   能较快适应。                                                                             
   工作强度       　         3.3                                                                                                                                                                                                                                              
                                                  　工作强度：我的工作内容充实、饱满。                                                                 
                                                   　价值特征：喜欢相对较为轻松的工作，不希望因工作强度给自己太多的压力。                                                                                                                                                    
 ©智联测评版权所有                                                                                         - 第  4 页/共   6  页  - 
</pre>

```java
@Data
public class ValuesItem implements PatternApplyAware {
    private String name;
    private String score;

    @Override public void apply(String[] groups) {
        this.name = groups[0];
        this.score = groups[1];
    }
}

List<ValuesItem> items = textMatcher.searchPattern("(\\S+)[　\\s]+(\\d+(?>\\.\\d+)?)", ValuesItem.class,
                new TextMatcherOption("3  详细结果", "©智联测评版权所有"));
assertThat(items.toString()).isEqualTo("[" +
        "ValuesItem(name=薪酬福利, score=8.3), " +
        "ValuesItem(name=工作稳定, score=6.1), " +
        "ValuesItem(name=公平公正, score=3.5), " +
        "ValuesItem(name=工作强度, score=3.3)]");


// 或者
val sb = new StringBuilder();
textMatcher.searchPattern("(\\S+)[　\\s]+(\\d+(?>\\.\\d+)?)",
        new PatternApplyAware() {
            @Override public void apply(String[] groups) {
                sb.append(groups[0]).append(":").append(groups[1]).append("\n");
            }
        },
        new TextMatcherOption("3  详细结果", "©智联测评版权所有"));

assertThat(sb.toString()).isEqualTo(
        "薪酬福利:8.3\n" +
                "工作稳定:6.1\n" +
                "公平公正:3.5\n" +
                "工作强度:3.3\n");
```

### 图片提取

```java
@Cleanup val is = new FileInputStream("演示.pdf");
List<PdfImage> pdfImages = PdfStripper.stripImages(is, 3, 3);
assertThat(pdfImages).hasSize(1);
val pdfImage = pdfImages.get(0);
assertThat(pdfImage.getName()).isEqualTo("Im37");
assertThat(pdfImage.getSuffix()).isEqualTo("jpg");
assertThat(pdfImage.getImage()).isNotNull();
assertThat(pdfImage.getHeight()).isGreaterThan(0);
assertThat(pdfImage.getWidth()).isGreaterThan(0);

Util.saveImage(pdfImage);
```

### 自定义提取

参考HogonPdfListener实现。

```java
HogonPdfListener pdfListener = new HogonPdfListener();
PdfStripper.stripCustom(is, PdfPagesSelect.onPages(1), pdfListener);
assertThat(pdfListener.itemScores()).isEqualTo("效度:4\n" +
        "调适.同理心:4\n" +
        "调适.不焦虑:4\n" +
        "调适.不内疚:4\n");
```

## PDF 分析的过程
1. 观察PDF的结构
2. 使用[PDF2HTML工具](http://cssbox.sourceforge.net/pdf2dom/download.php)，生成HTML页面，分析DOM结构

### 演示
![image](https://user-images.githubusercontent.com/1940588/45659529-96079c80-bb27-11e8-97ff-277bb39a701d.png)

第一页采用基于位置的文本提取器PDFLayoutTextStripper来识别，然后使用正则表达式`\b([^\s\d]+)[\s\r\n]+(\d?\d)\b`来获取数据。
```
调适:98
抱负:73
社交:74
人际敏感度:69
审慎:72
好奇:90
学习方式:73
激动:99
多疑:99
谨慎:86
内敛:93
消极:97
自大:43
狡猾:49
戏剧化:34
幻想:96
苛求:38
恭顺:5
认可:22
权力:86
享乐:98
利他:96
归属:94
传统:36
保障:40
商业:79
美感:38
科学:86
```

![image](https://user-images.githubusercontent.com/1940588/45659559-b46d9800-bb27-11e8-8cab-567cf8473e58.png)

第二页则采用文档生成拦截器，拦截指定字号的文本与长方形(rect)，根据顺序来获取其相关性。
```
效度:4
调适.同理心:4
调适.不焦虑:4
调适.不内疚:4
调适.冷静:4
调适.性情平和:4
调适.无抱怨:4
调适.信赖他人:3
调适.依附感:4
抱负.好竞争:4
抱负.自信:4
抱负.成就感:1
抱负.领导力:4
抱负.认同:4
抱负.无社交焦虑:4
社交.喜欢派对:3
社交.喜欢人群:4
社交.寻求体验:4
社交.表现欲:2
社交.风趣:4
人际敏感度.容易相处:4
人际敏感度.敏感:2
人际敏感度.关怀:4
人际敏感度.喜欢他人:4
人际敏感度.无恶意:3
审慎.循规蹈矩:3
审慎.精通掌握:4
审慎.尽责:3
审慎.不独立自主:4
审慎.计划性:2
审慎.控制冲动:2
审慎.避免麻烦:4
好奇.科学能力:4
好奇.好奇心:4
好奇.寻求刺激:4
好奇.智力游戏:4
好奇.想出主意:4
好奇.文化:0
学习方式.教育:4
学习方式.数学能力:4
学习方式.记忆力:4
学习方式.阅读:2
激动.情绪无常:4
激动.容易失望:4
激动.缺乏目标:4
多疑.愤世嫉俗:4
多疑.不信任/猜忌:4
多疑.记仇:4
谨慎.回避:4
谨慎.胆怯:4
谨慎.不果断坚决:2
内敛.内向:3
内敛.不善交际:4
内敛.强硬:3
消极.消极抵抗:4
消极.不被赏识:4
消极.容易激怒:4
自大.特权:1
自大.过度自信:2
自大.自认天资聪颖:3
狡猾.爱冒险:1
狡猾.冲动:4
狡猾.工于心计:2
戏剧化.在公众场合自信:2
戏剧化.容易分心:3
戏剧化.自我炫耀:1
幻想.想法怪异:4
幻想.异常敏感:3
幻想.创造性思维:2
苛求.高标准:1
苛求.完美主义:2
苛求.有条理:2
恭顺.优柔寡断:1
恭顺.阿谀奉承:2
恭顺.顺从依赖:1
认可.生活方式:1
认可.信念:2
认可.职业偏好:0
认可.反感:1
认可.偏好的共事者:4
权力.生活方式:4
权力.信念:4
权力.职业偏好:3
权力.反感:3
权力.偏好的共事者:4
享乐.生活方式:4
享乐.信念:4
享乐.职业偏好:3
享乐.反感:4
享乐.偏好的共事者:4
利他.生活方式:4
利他.信念:4
利他.职业偏好:3
利他.反感:4
利他.偏好的共事者:4
归属.生活方式:4
归属.信念:4
归属.职业偏好:4
归属.反感:4
归属.偏好的共事者:3
传统.生活方式:3
传统.信念:1
传统.职业偏好:3
传统.反感:2
传统.偏好的共事者:2
保障.生活方式:3
保障.信念:2
保障.职业偏好:4
保障.反感:1
保障.偏好的共事者:3
商业.生活方式:2
商业.信念:4
商业.职业偏好:4
商业.反感:2
商业.偏好的共事者:4
美感.生活方式:0
美感.信念:3
美感.职业偏好:4
美感.反感:3
美感.偏好的共事者:2
科学.生活方式:3
科学.信念:4
科学.职业偏好:4
科学.反感:4
科学.偏好的共事者:3
```
