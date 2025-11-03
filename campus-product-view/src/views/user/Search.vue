<template>
    <div>
        关键词：{{ searchKey }}
    </div>
</template>

<script>
import { getSearchKey } from "@/utils/storage";
export default {
    data() {
        return {
            searchKey: '',
            keyInterval: null // 用于存储定时器的引用
        };
    },
    created() {
        this.startKeyLoader(); // 启动定时器
    },
    beforeDestroy() {
        this.clearKeyLoader(); // 清除定时器
    },
    methods: {
        loadKey() {
            const key = getSearchKey();
            if (key !== this.searchKey) {
                console.log("进行搜索");
                
                this.searchKey = key;
            }
        },
        startKeyLoader() {
            // 每隔一定时间调用 loadKey 方法
            this.keyInterval = setInterval(() => {
                this.loadKey();
            }, 1000); // 每1000毫秒（1秒）调用一次
        },
        clearKeyLoader() {
            // 清除定时器
            if (this.keyInterval) {
                clearInterval(this.keyInterval);
                this.keyInterval = null; // 重置定时器引用
            }
        }
    }
};
</script>
