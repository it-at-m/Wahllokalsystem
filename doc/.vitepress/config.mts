import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Wahllokalsystem",
  description: "election supporting system",
  srcDir: 'src', //markdown files are located in that directory
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: 'About', link: '/about/' },
      { text: 'Features', link: '/features/' },
      { text: 'Technik', link: '/technik/' }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/it-at-m/Wahllokalsystem/' }
    ]
  }
})
