Spring Batch
============

Spring Batchとは
-----------------

-   バッチアプリケーションの共通要求項目を効果的に解決するバッチ基盤Open-Sourceフレームワークの提供を目標で、SpringSource社とAccenture社が共同で開発
-   特定のDomainに使うためではなく、汎用的に使うために作られた。
-   多様なバッチを全部考えてデザインしたので、かなり複雑化も
    -   簡単な作業をバッチで処理しようとするだけだと、予想より結構なコストになるかも

##### Spring Batchの長所
-   Accenture社の参加で、現場の経験を基盤で設計された
-   明確な責任の分離で、拡張にいい
-   Job Repositoryを利用してバッチ作業の情報を残してるため、メンテナンスや管理が容易

##### Spring Batchの概念
-   Job
    -   一つのバッチ作業  
-   Job Instance
    -   バッチ実行時のそれぞれの実行を表現。つまり、Batch Jobが毎週実行されるとしたら、それぞれの実行されるBatch JobをJob Instanceとする
-   Job Execution
    -   バッチの実行時、実際実行されたExecutionの事。午前中にBatch Jobを実行したとして、最初失敗して二回目に成功だったら、それは別々のJob Executionになるが、同じ作業を実行したので、同じJob Instanceとなる
-   Job Parameter
    -   毎回のバッチ作業で渡されるParameterの事。

###### Batch Jobの構成
-   Step
    -   SpringでBatch JobはStepの集まり。Job内でStepは順番的に実行される
-   Tasklet
    -   各Stepで実行されるLogic。Spring-Batchでは「Reader」「Processor」「Writer」Interfaceを提供するが、開発者がカスタムで作ってもよい


開発環境構築とサンプルの実行
--------------------------
### 目標
-   Spring Batchの開発環境構築
    -   環境構築の方法は色々あるが、ここではGradleを使って環境構築
-   サンプルバッチの実行によるDB更新
    -   PostgreSQLでSpring Batchを回し、その結果を確認する

### 開発環境構築
##### 基本設定
-   [Gradle](https://gradle.org/install)と[JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)インストールされていればOK
-   すべてのソースは、[ここ](https://github.com/idenrai/springbatch)にあるので、```git clone https://github.com/idenrai/springbatch.git``` でもOK

##### プロジェクトの構成

```Text
C:\springbat
│  README.md
│  build.gradle
│  
└─src/main
    │
    ├─java/com/test/batch
    │   Batch.java
    │   Fruit.java
    │   FruitItemProcessor.java
    │   JobStartEndListener.java
    │   SpringBatchApplication.java
    │          
    └─resource
        application.yml
        fruit_price.csv
        fruit_price2.csv
        schema-all.sql
```
