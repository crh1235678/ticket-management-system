export type ArticleType = 'OVERSEAS' | 'DOMESTIC' | 'CITY' | 'COUNTRYSIDE';

export interface ArticleCatalogVO {
    id: number;
    title: string;
    description: string;
    coverImg: string;
    articleType: ArticleType;
    viewCount: number;
    likeCount: number;
}


export interface ArticleDetailVO {
    id: number;
    title: string;
    content: string;
    articleType: ArticleType;
    viewCount: number;
    likeCount: number;
    createTime: string; 
}