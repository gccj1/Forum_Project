import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios";
import { createPinia } from "pinia";
import 'element-plus/theme-chalk/dark/css-vars.css'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
    faThumbsDown,
    faThumbsUp,
    faCalendarCheck,
    faEdit,
    faTurnUp,
    faArrowAltCircleUp,
    faComments
} from '@fortawesome/free-solid-svg-icons'
library.add(faThumbsDown,faThumbsUp,faCalendarCheck,faEdit,faTurnUp,faArrowAltCircleUp,faComments)
axios.defaults.baseURL = 'http://localhost:8080'
import {Quill} from 'vue-quill-editor'
window.Quill = Quill

const scriptEl = document.createElement('script');
scriptEl.charset = 'utf-8'
scriptEl.src =  './image-resize.min.js'
const head = document.head || document.getElementsByTagName('head')[0];
head.appendChild(scriptEl);
const app = createApp(App)
app.component('font-awesome-icon', FontAwesomeIcon)
app.use(createPinia())
app.use(router)
app.mount('#app')
